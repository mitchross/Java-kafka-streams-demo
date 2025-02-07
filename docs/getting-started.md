# Getting Started

This guide will help you set up and run the Real-Time Product Analytics platform.

## Prerequisites

Ensure you have the following installed:
- Java 17 or higher
- Docker and Docker Compose
- Gradle
- Git

## Quick Setup

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/kafka-streams-demo
cd kafka-streams-demo
```

### 2. Start Kafka Environment

```bash
# Start all services
docker-compose up -d

# Wait for about 30 seconds for all services to be ready
# You can monitor readiness in Control Center: http://localhost:9021
```

The following topics are automatically created:
- `product-events`: User interactions with products
- `product-metadata`: Product details and information
- `product-analytics`: Aggregated analytics results

### 3. Verify Setup

Check that all services are running:
```bash
docker ps

# Expected output:
# - broker
# - zookeeper
# - schema-registry
# - control-center
# - kafka-ui
```

Verify topic creation:
```bash
docker exec broker kafka-topics --list --bootstrap-server localhost:9092
```

## Running the Application

### 1. Load Sample Data

```bash
# Load product metadata
./gradlew loadProductMetadata
```

Example metadata record:
```json
{
  "productId": "P123",
  "name": "Premium Headphones",
  "category": "Electronics",
  "basePrice": 199.99
}
```

### 2. Start the Application

```bash
# Run with debug logging enabled
LOGGING_LEVEL_ROOT=DEBUG ./gradlew bootRun
```

### 3. Generate Test Events

In a new terminal:
```bash
# Generate sample product events
./gradlew generateTestData
```

### 4. Monitor Results

View the analytics output:
```bash
docker exec -it broker kafka-console-consumer \
    --bootstrap-server localhost:9092 \
    --topic product-analytics \
    --from-beginning
```

## Management Interfaces

### Confluent Control Center
- URL: http://localhost:9021
- Features:
  - Topic management
  - Consumer group monitoring
  - Performance metrics
  - Schema registry

### Kafka UI
- URL: http://localhost:8080
- Features:
  - Topic browsing
  - Message inspection
  - Cluster monitoring

## Example Output

### Product Event
```json
{
  "productId": "P123",
  "userId": "U789",
  "eventType": "VIEW",
  "timestamp": 1648656000000,
  "value": 199.99
}
```

### Analytics Result
```json
{
  "productId": "P123",
  "name": "Premium Headphones",
  "category": "Electronics",
  "viewCount": 145,
  "uniqueUsers": 89,
  "totalValue": 1799.91,
  "conversionRate": 0.12,
  "windowStart": 1648656000000,
  "windowEnd": 1648656300000
}
```

## Performance Testing

Run high-volume test:
```bash
./gradlew generateLoadTest
```

Monitor performance:
1. Open Control Center (http://localhost:9021)
2. Navigate to Topics → product-analytics
3. View message throughput and latency metrics

## Next Steps

- Check out the [Examples](examples/index.md) for specific use cases
- Learn about [Monitoring](monitoring.md) your application
- Review the [Troubleshooting Guide](troubleshooting.md) for common issues 