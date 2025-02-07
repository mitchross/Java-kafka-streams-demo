# Java Streams Demo

## Business Problem: E-commerce Inventory and Pricing Analysis

Imagine you're running an e-commerce platform like Amazon, and you need to:
1. Combine product data from multiple sources (catalog, pricing, and warehouse systems)
2. Calculate real-time inventory levels across multiple warehouses
3. Analyze pricing patterns by product category
4. Process millions of product updates efficiently

### Real-World Example

Consider a product like "Premium Headphones":

**Starting Data:**
```json
// Basic Product Info (from catalog system)
{
    "id": "P123",
    "name": "Premium Headphones",
    "category": "Electronics",
    "description": "High-end wireless headphones"
}

// Price Data (from pricing system)
{
    "productId": "P123",
    "amount": 199.99,
    "currency": "USD"
}

// Stock Data (from multiple warehouses)
[
    {"productId": "P123", "warehouseId": "NYC", "quantity": 120},
    {"productId": "P123", "warehouseId": "LA", "quantity": 85}
]
```

**What This Demo Does:**
1. Combines these separate data sources into enriched products
2. Calculates total stock across all warehouses (205 units)
3. Groups products by category to show:
   - Average price in Electronics category
   - Total inventory value ($40,997.95)
   - Stock distribution across warehouses

**Business Value:**
- Inventory managers can see total stock across warehouses
- Pricing teams can analyze price ranges by category
- Business analysts can identify high-value inventory
- Operations can optimize warehouse distribution

This demo shows how to perform these operations efficiently using Java Streams, handling:
- Large datasets (1M+ products)
- Real-time processing requirements
- Memory-efficient operations
- Thread-safe concurrent processing

## Technical Overview

This project demonstrates efficient data processing using Java Streams API, showcasing parallel processing, memory efficiency, and functional programming patterns.

## Overview

The demo processes product data through several stages:
1. Product information enrichment
2. Price aggregation
3. Stock level analysis
4. Category-based statistics

### High-Level Architecture Overview

From an architectural perspective, this system operates in several distinct layers:

#### 1. Data Generation Layer
- **Product Data Generation**
  - Source: In-memory data generator
  - Volume: Configurable (1K to 1M records)
  - Data: Basic product information (id, name, category)

- **Price & Stock Generation**
  - Source: Random data generation
  - Distribution: Controlled random distribution
  - Data: Prices and warehouse stock levels

#### 2. Processing Layer
- **Data Enrichment**
  - Parallel stream processing
  - Product-price-stock joining
  - Memory-efficient operations

- **State Management**
  - In-memory aggregations
  - Thread-safe collections
  - Concurrent hash maps

#### 3. Analytics Layer
- **Category Analysis**
  - Price statistics by category
  - Product distribution analysis
  - Performance metrics

- **Warehouse Analytics**
  - Stock level aggregations
  - Cross-warehouse analysis
  - Inventory insights

#### 4. Output Layer
- **Results Generation**
  - Statistical summaries
  - Performance metrics
  - Memory usage stats

#### System Components
1. **Core Models**
   - Product (immutable record)
   - Price (immutable record)
   - Stock (immutable record)
   - EnrichedProduct (mutable for aggregation)

2. **Processing Components**
   - DataGenerationService
   - ProductEnrichmentProcessor
   - Performance monitoring

#### Performance Considerations
- Parallel stream execution
- Memory-efficient data structures
- Thread-safe operations
- Configurable batch sizes

## Key Features

### 1. Parallel Processing
- Uses `parallelStream()` for concurrent data processing
- Leverages the fork-join pool for optimal CPU utilization
- Demonstrates scalability with different dataset sizes

### 2. Memory Efficiency
- Lazy evaluation of streams
- Efficient data structures (ConcurrentHashMap)
- Minimal object creation
- Stream pipeline optimization

### 3. Stream Operations
- Map/Reduce operations
- Filtering and transformation
- Complex aggregations
- Grouping and summarizing

### 4. Data Models
- Immutable records for core data (Product, Price, Stock)
- Mutable enriched model for aggregation (EnrichedProduct)
- Thread-safe collections for concurrent operations

## Project Structure
```
src/
├── main/
│   └── java/
│       └── com/
│           └── demo/
│               ├── model/
│               │   ├── Product.java         # Core product data
│               │   ├── Price.java          # Price information
│               │   ├── Stock.java          # Warehouse stock data
│               │   └── EnrichedProduct.java # Combined product info
│               ├── processor/
│               │   └── ProductEnrichmentProcessor.java # Main processing logic
│               ├── service/
│               │   └── DataGenerationService.java     # Test data generation
│               └── Main.java               # Application entry point
```

## Key Components

### 1. Data Models
- **Product**: Basic product information (id, name, category, description)
- **Price**: Product pricing data (productId, amount, currency)
- **Stock**: Warehouse inventory (productId, warehouseId, quantity)
- **EnrichedProduct**: Combined product information with aggregated data

### 2. Processing Pipeline
```java
products.parallelStream()
    .map(this::enrichProduct)        // Enrich with price and stock
    .filter(p -> p.getPrice() != null) // Filter valid products
    .collect(groupingByConcurrent(   // Group by category
        EnrichedProduct::getCategory,
        summarizingDouble(p -> p.getPrice().doubleValue())
    ));
```

### 3. Performance Features
- Parallel processing for large datasets
- Concurrent collections for thread safety
- Efficient data structures for lookups
- Optimized stream operations

## Running the Demo

### Prerequisites
- Java 17 or higher
- Gradle

### Build and Run
```bash
# Build the project
./gradlew build

# Run the main application
./gradlew run

# Run performance tests
./gradlew performanceTest
```

### Sample Output
```
Testing with dataset size: 1,000
Category: Electronics, Avg Price: $495.32, Total Products: 203
Category: Books, Avg Price: $234.56, Total Products: 198
Warehouse: NYC, Total Stock: 45,678
Warehouse: LA, Total Stock: 32,456
Processed 1,000 products in 234 ms

Testing with dataset size: 10,000
...
```

## Performance Tuning

### 1. Parallel Processing
Control the fork-join pool size:
```bash
java -Djava.util.concurrent.ForkJoinPool.common.parallelism=4 -jar build/libs/java-streams-demo.jar
```

### 2. Memory Settings
Adjust heap size for large datasets:
```bash
java -Xmx2g -jar build/libs/java-streams-demo.jar
```

## Stream Operations Showcase

### 1. Mapping and Filtering
```java
products.stream()
    .filter(p -> p.getPrice().compareTo(BigDecimal.ZERO) > 0)
    .map(this::enrichProduct)
```

### 2. Grouping and Aggregation
```java
products.stream()
    .collect(groupingBy(
        Product::category,
        summarizingDouble(p -> p.getPrice().doubleValue())
    ))
```

### 3. Parallel Processing
```java
products.parallelStream()
    .flatMap(p -> p.getStocks().stream())
    .collect(groupingByConcurrent(Stock::warehouseId))
```

## Best Practices Demonstrated

1. **Immutable Data**
   - Records for core models
   - Thread-safe operations

2. **Efficient Processing**
   - Parallel streams for large datasets
   - Appropriate collectors for different scenarios
   - Memory-efficient operations

3. **Clean Code**
   - Functional programming patterns
   - Clear separation of concerns
   - Descriptive naming

4. **Performance Optimization**
   - Concurrent collections where appropriate
   - Efficient data structures for lookups
   - Controlled parallelism