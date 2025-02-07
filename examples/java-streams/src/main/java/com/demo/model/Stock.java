package com.demo.model;

public record Stock(
    String productId,
    String warehouseId,
    int quantity
) {} 