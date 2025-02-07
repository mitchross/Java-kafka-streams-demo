package com.demo.processor;

import com.demo.model.EnrichedProduct;
import com.demo.model.Price;
import com.demo.model.Product;
import com.demo.model.Stock;
import lombok.extern.slf4j.Slf4j;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class ProductEnrichmentProcessor {
    
    public List<EnrichedProduct> enrichProducts(
            List<Product> products,
            List<Price> prices,
            List<Stock> stocks) {
        
        // Create a map for faster price lookups
        Map<String, Price> priceMap = prices.stream()
            .collect(Collectors.toConcurrentMap(
                Price::productId,
                price -> price
            ));
        
        // Create a map for stock lookups
        Map<String, List<Stock>> stockMap = stocks.stream()
            .collect(Collectors.groupingByConcurrent(Stock::productId));
        
        // Process products in parallel
        return products.parallelStream()
            .map(product -> {
                EnrichedProduct enriched = new EnrichedProduct(product);
                
                // Add price information
                Price price = priceMap.get(product.id());
                if (price != null) {
                    enriched.addPrice(price);
                }
                
                // Add stock information
                List<Stock> productStocks = stockMap.get(product.id());
                if (productStocks != null) {
                    productStocks.forEach(enriched::addStock);
                }
                
                return enriched;
            })
            .toList();
    }
    
    public Map<String, DoubleSummaryStatistics> analyzeProductsByCategory(List<EnrichedProduct> products) {
        return products.parallelStream()
            .filter(p -> p.getPrice() != null)
            .collect(Collectors.groupingByConcurrent(
                EnrichedProduct::getCategory,
                Collectors.summarizingDouble(p -> p.getPrice().doubleValue())
            ));
    }
    
    public Map<String, Integer> calculateWarehouseTotals(List<EnrichedProduct> products) {
        return products.parallelStream()
            .flatMap(p -> p.getWarehouseStock().entrySet().stream())
            .collect(Collectors.groupingByConcurrent(
                Map.Entry::getKey,
                Collectors.summingInt(Map.Entry::getValue)
            ));
    }
    
    public void processLargeDataset(int size) {
        log.info("Starting large dataset processing of {} items", size);
        long startTime = System.currentTimeMillis();
        
        // Create sample data
        com.demo.service.DataGenerationService generator = new com.demo.service.DataGenerationService();
        List<Product> products = generator.generateProducts(size);
        List<Price> prices = generator.generatePrices(products);
        List<Stock> stocks = generator.generateStocks(products);
        
        // Process data
        List<EnrichedProduct> enrichedProducts = enrichProducts(products, prices, stocks);
        Map<String, DoubleSummaryStatistics> categoryStats = analyzeProductsByCategory(enrichedProducts);
        Map<String, Integer> warehouseTotals = calculateWarehouseTotals(enrichedProducts);
        
        long endTime = System.currentTimeMillis();
        log.info("Processed {} products in {} ms", size, (endTime - startTime));
        
        // Log some statistics
        categoryStats.forEach((category, stats) -> 
            log.info("Category: {}, Avg Price: ${}, Total Products: {}", 
                category, String.format("%.2f", stats.getAverage()), stats.getCount())
        );
        
        warehouseTotals.forEach((warehouse, total) ->
            log.info("Warehouse: {}, Total Stock: {}", warehouse, total)
        );
    }
} 