package com.demo.model;

import java.math.BigDecimal;

public record Price(
    String productId,
    BigDecimal amount,
    String currency
) {} 