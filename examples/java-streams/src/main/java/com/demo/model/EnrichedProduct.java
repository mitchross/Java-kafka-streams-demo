package com.demo.model;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class EnrichedProduct {
    private final String id;
    private final String name;
    private final String category;
    private final String description;
    private BigDecimal price;
    private String currency;
    private Map<String, Integer> warehouseStock;

    public EnrichedProduct(Product product) {
        this.id = product.id();
        this.name = product.name();
        this.category = product.category();
        this.description = product.description();
        this.warehouseStock = new ConcurrentHashMap<>();
    }

    public void addPrice(Price price) {
        this.price = price.amount();
        this.currency = price.currency();
    }

    public void addStock(Stock stock) {
        warehouseStock.put(stock.warehouseId(), stock.quantity());
    }

    public int getTotalStock() {
        return warehouseStock.values().stream()
            .mapToInt(Integer::intValue)
            .sum();
    }
} 