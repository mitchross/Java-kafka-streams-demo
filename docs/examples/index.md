# Examples & Usage Patterns

This guide provides practical examples and common usage patterns for the Real-Time Product Analytics platform.

## Basic Examples

### 1. Product Event Processing

#### Sending Product View Events
```java
// Create a product view event
ProductViewEvent event = ProductViewEvent.builder()
    .productId("PROD-123")
    .userId("USER-456")
    .timestamp(System.currentTimeMillis())
    .sessionId("SESSION-789")
    .build();

// Send to Kafka topic
kafkaTemplate.send("product-views", event.getProductId(), event);
```

#### Processing View Events
```java
@Bean
public KStream<String, ProductViewEvent> processProductViews(StreamsBuilder builder) {
    return builder.stream("product-views", 
        Consumed.with(Serdes.String(), productViewEventSerde))
        .peek((key, value) -> log.info("Processing view: {}", value))
        .groupByKey()
        .windowedBy(TimeWindows.of(Duration.ofMinutes(5)))
        .count()
        .toStream()
        .peek((key, value) -> log.info("View count: {} = {}", key, value));
}
```

### 2. Product Analytics

#### Calculating View-to-Purchase Ratio
```java
@Bean
public KTable<String, Double> calculateConversionRate(
        KTable<String, Long> viewCounts,
        KTable<String, Long> purchaseCounts) {
    
    return viewCounts.join(
        purchaseCounts,
        (views, purchases) -> {
            if (views == 0) return 0.0;
            return (double) purchases / views;
        },
        Materialized.as("conversion-rates")
    );
}
```

#### Trending Products Analysis
```java
@Bean
public KStream<String, ProductTrend> analyzeTrends(
        KStream<String, ProductViewEvent> viewEvents) {
    
    return viewEvents
        .groupByKey()
        .windowedBy(
            SlidingWindows.withTimeDifferenceAndGrace(
                Duration.ofMinutes(30),
                Duration.ofMinutes(5)
            )
        )
        .count()
        .toStream()
        .mapValues(count -> new ProductTrend(
            count,
            System.currentTimeMillis()
        ));
}
```

## Advanced Examples

### 1. Complex Event Processing

#### Session-Based Analysis
```java
@Bean
public KStream<String, SessionAnalytics> analyzeUserSessions(
        KStream<String, ProductViewEvent> viewEvents) {
    
    return viewEvents
        .groupBy((key, value) -> value.getSessionId())
        .windowedBy(SessionWindows.with(Duration.ofMinutes(30)))
        .aggregate(
            SessionAnalytics::new,
            (key, value, aggregate) -> aggregate.addEvent(value),
            Materialized.as("session-analytics")
        )
        .toStream()
        .map((key, value) -> KeyValue.pair(
            key.key(),
            value
        ));
}
```

#### Category Performance
```java
@Bean
public KTable<String, CategoryStats> analyzeCategoryPerformance(
        KStream<String, ProductViewEvent> viewEvents,
        KTable<String, Product> products) {
    
    return viewEvents
        .join(
            products,
            (event, product) -> new ProductActivity(event, product)
        )
        .groupBy((key, value) -> value.getProduct().getCategory())
        .aggregate(
            CategoryStats::new,
            (key, value, aggregate) -> aggregate.update(value),
            Materialized.as("category-stats")
        );
}
```

### 2. Data Enrichment

#### Product Metadata Enrichment
```java
@Bean
public KStream<String, EnrichedProduct> enrichProducts(
        KStream<String, Product> products,
        KTable<String, PriceInfo> prices,
        KTable<String, InventoryLevel> inventory) {
    
    return products
        .join(
            prices,
            (product, price) -> product.withPrice(price)
        )
        .join(
            inventory,
            (product, stock) -> EnrichedProduct.builder()
                .product(product)
                .currentStock(stock)
                .build()
        );
}
```

#### Real-time Recommendations
```java
@Bean
public KStream<String, ProductRecommendation> generateRecommendations(
        KStream<String, ProductViewEvent> viewEvents,
        KTable<String, Product> products) {
    
    return viewEvents
        .groupBy((key, value) -> value.getUserId())
        .windowedBy(TimeWindows.of(Duration.ofHours(24)))
        .aggregate(
            UserProfile::new,
            (key, value, aggregate) -> aggregate.addView(value),
            Materialized.as("user-profiles")
        )
        .toStream()
        .flatMapValues(profile -> profile.generateRecommendations());
}
```

## Integration Examples

### 1. External System Integration

#### REST API Integration
```java
@Bean
public KStream<String, EnrichedProduct> integrateExternalPricing(
        KStream<String, Product> products) {
    
    return products.mapValues(product -> {
        PriceInfo price = restTemplate.getForObject(
            "http://pricing-service/prices/" + product.getId(),
            PriceInfo.class
        );
        return product.withPrice(price);
    });
}
```

#### Database Integration
```java
@Bean
public KStream<String, Product> persistToDatabase(
        KStream<String, Product> products) {
    
    return products.peek((key, product) -> {
        jdbcTemplate.update(
            "INSERT INTO products (id, name, price) VALUES (?, ?, ?)",
            product.getId(),
            product.getName(),
            product.getPrice()
        );
    });
}
```

### 2. Monitoring Integration

#### Metrics Collection
```java
@Bean
public KStream<String, ProductViewEvent> collectMetrics(
        KStream<String, ProductViewEvent> viewEvents) {
    
    return viewEvents.peek((key, event) -> {
        meterRegistry.counter("product.views", 
            "product", event.getProductId(),
            "category", event.getCategory()
        ).increment();
    });
}
```

#### Alert Generation
```java
@Bean
public KStream<String, Alert> generateAlerts(
        KTable<String, InventoryLevel> inventory) {
    
    return inventory
        .toStream()
        .filter((key, level) -> level.getQuantity() < level.getMinThreshold())
        .mapValues(level -> Alert.builder()
            .type(AlertType.LOW_STOCK)
            .productId(key)
            .quantity(level.getQuantity())
            .threshold(level.getMinThreshold())
            .build()
        );
}
```

## Testing Examples

### 1. Unit Testing

#### Testing Topology
```java
@Test
public void testProductViewProcessing() {
    // Create topology
    Topology topology = createProductViewTopology();
    
    // Create test driver
    TopologyTestDriver testDriver = new TopologyTestDriver(
        topology, 
        getTestProperties()
    );
    
    // Create test record
    ProductViewEvent event = createTestEvent();
    TestInputTopic<String, ProductViewEvent> inputTopic = 
        testDriver.createInputTopic("product-views",
            new StringSerializer(),
            new JsonSerializer<>());
    
    // Send test record
    inputTopic.pipeInput(event.getProductId(), event);
    
    // Verify output
    TestOutputTopic<String, Long> outputTopic =
        testDriver.createOutputTopic("view-counts",
            new StringDeserializer(),
            new LongDeserializer());
    
    assertThat(outputTopic.readKeyValue())
        .isEqualTo(KeyValue.pair(event.getProductId(), 1L));
}
```

#### Testing State Stores
```java
@Test
public void testStateStore() {
    // Create topology with state store
    Topology topology = createTopologyWithStateStore();
    
    // Create test driver
    TopologyTestDriver testDriver = new TopologyTestDriver(
        topology, 
        getTestProperties()
    );
    
    // Get state store
    KeyValueStore<String, Long> store =
        testDriver.getKeyValueStore("view-counts");
    
    // Verify state
    assertThat(store.get("PROD-123")).isEqualTo(5L);
}
```

### 2. Integration Testing

#### Testing with Embedded Kafka
```java
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"product-views"})
class ProductAnalyticsIntegrationTest {

    @Autowired
    private KafkaTemplate<String, ProductViewEvent> kafkaTemplate;
    
    @Autowired
    private ProductAnalyticsService analyticsService;
    
    @Test
    void testProductViewProcessing() {
        // Send test event
        ProductViewEvent event = createTestEvent();
        kafkaTemplate.send("product-views", event.getProductId(), event);
        
        // Verify processing
        await()
            .atMost(5, TimeUnit.SECONDS)
            .until(() -> analyticsService.getViewCount(
                event.getProductId()) == 1);
    }
}
```

## Configuration Examples

### 1. Kafka Streams Configuration

#### Basic Configuration
```yaml
spring:
  kafka:
    streams:
      application-id: product-analytics
      bootstrap-servers: localhost:9092
      properties:
        processing.guarantee: exactly_once
        num.stream.threads: 4
        state.dir: /tmp/kafka-streams
```

#### Advanced Configuration
```yaml
spring:
  kafka:
    streams:
      properties:
        # Processing
        max.task.idle.ms: 1000
        commit.interval.ms: 30000
        cache.max.bytes.buffering: 10485760
        
        # State Store
        state.cleanup.delay.ms: 600000
        windowstore.changelog.additional.retention.ms: 86400000
        
        # Performance
        num.standby.replicas: 1
        rocksdb.config.setter: com.example.CustomRocksDBConfig
```

## Next Steps

- Review [Architecture](../architecture.md) for system design details
- Check [Monitoring Guide](../monitoring.md) for operational insights
- See [Troubleshooting Guide](../troubleshooting.md) for problem resolution 