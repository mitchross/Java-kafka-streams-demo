# Java Streams Demo

This project demonstrates efficient data processing using Java Streams API, providing a comparison with the main Kafka Streams implementation.

## Documentation

For detailed documentation, see the [Examples & Usage Patterns](https://mitchross.github.io/kafka-streams-demo/examples/#java-streams-demo) section in our main documentation.

## Quick Overview

This demo shows how to:
- Process large datasets efficiently using Java Streams
- Combine data from multiple sources (products, prices, stock)
- Calculate analytics using parallel processing
- Handle memory-efficient operations
- Implement thread-safe processing

## Key Differences from Main Project
- In-memory processing vs. distributed streaming
- Single JVM vs. scalable cluster
- Batch processing vs. continuous streaming
- Local state vs. distributed state stores

## Running the Demo

```bash
# Build and run
./gradlew run

# Run performance tests
./gradlew performanceTest
```

## Features
- Parallel stream processing
- Memory-efficient data handling
- Complex aggregations and transformations
- Performance benchmarking with various dataset sizes

## Project Structure
```
src/
├── main/
│   └── java/
│       └── com/
│           └── demo/
│               ├── model/          # Data models
│               ├── processor/      # Processing logic
│               ├── service/        # Test data generation
│               └── Main.java       # Entry point
```

For more details on implementation, usage patterns, and performance characteristics, please refer to our [main documentation](https://mitchross.github.io/kafka-streams-demo/examples/#java-streams-demo).