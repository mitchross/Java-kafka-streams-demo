package com.demo.service;

import com.demo.model.Product;
import com.demo.model.Price;
import com.demo.model.Stock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.UUID;

public class DataGenerationService {
    private static final Random RANDOM = new Random();
    private static final String[] CATEGORIES = {"Electronics", "Books", "Clothing", "Food", "Toys"};
    private static final String[] WAREHOUSES = {"NYC", "LA", "CHI", "HOU", "PHX"};

    public List<Product> generateProducts(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> new Product(
                "P" + i,
                "Product " + i,
                CATEGORIES[RANDOM.nextInt(CATEGORIES.length)],
                "Description for product " + i
            ))
            .toList();
    }

    public List<Price> generatePrices(List<Product> products) {
        return products.stream()
            .map(p -> new Price(
                p.id(),
                BigDecimal.valueOf(10 + RANDOM.nextDouble() * 990).setScale(2, BigDecimal.ROUND_HALF_UP),
                "USD"
            ))
            .toList();
    }

    public List<Stock> generateStocks(List<Product> products) {
        return products.stream()
            .flatMap(p -> IntStream.range(0, RANDOM.nextInt(1, WAREHOUSES.length))
                .mapToObj(i -> new Stock(
                    p.id(),
                    WAREHOUSES[i],
                    RANDOM.nextInt(1000)
                )))
            .toList();
    }
} 