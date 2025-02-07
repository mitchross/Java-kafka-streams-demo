# Monitoring & Management

This guide covers monitoring, managing, and maintaining your Real-Time Product Analytics platform.

## Monitoring Tools

### 1. Confluent Control Center
- **Access**: http://localhost:9021
- **Key Features**:
  - Real-time monitoring dashboard
  - Consumer lag tracking
  - Topic management
  - Schema registry
  - KSQL interface

### 2. Kafka UI
- **Access**: http://localhost:8080
- **Key Features**:
  - Lightweight interface
  - Message browser
  - Topic management
  - Consumer group monitoring

## Key Metrics to Monitor

### 1. Processing Metrics
- **Event Throughput**
  - Events processed per second
  - Batch processing rates
  - Peak throughput times

- **Processing Latency**
  - End-to-end latency
  - Window computation time
  - State store access time

### 2. Resource Metrics
- **Memory Usage**
  - JVM heap utilization
  - RocksDB memory usage
  - Cache hit rates

- **CPU Utilization**
  - Thread pool usage
  - GC activity
  - Processing overhead

### 3. Stream Metrics
- **Consumer Lag**
  - Per-partition lag
  - Catch-up rates
  - Rebalancing events

- **Topic Metrics**
  - Message rates
  - Partition distribution
  - Retention compliance

## Monitoring Dashboard

### Control Center Views

#### 1. Topic Overview
![Topic Overview](images/topic-overview.png)
- Message throughput
- Partition distribution
- Storage usage

#### 2. Consumer Groups
![Consumer Groups](images/consumer-groups.png)
- Consumer lag
- Processing rates
- Partition assignments

#### 3. Stream Metrics
![Stream Metrics](images/stream-metrics.png)
- Processing throughput
- State store metrics
- Window statistics

## Alert Configuration

### 1. System Alerts
- High consumer lag (> 1000 messages)
- Processing latency (> 500ms)
- Resource utilization (> 80%)

### 2. Business Alerts
- Unusual event patterns
- High error rates
- Data quality issues

## Health Checks

### 1. Application Health
```bash
# Check application status
curl http://localhost:8080/actuator/health

# Expected response:
{
    "status": "UP",
    "components": {
        "kafka": "UP",
        "stateStores": "UP"
    }
}
```

### 2. Kafka Health
```bash
# Check broker status
docker exec broker kafka-topics --list --bootstrap-server localhost:9092

# Check consumer group status
docker exec broker kafka-consumer-groups \
    --bootstrap-server localhost:9092 \
    --describe --group product-analytics
```

## Log Analysis

### 1. Application Logs
```bash
# View application logs
docker logs kafka-streams-demo

# Stream logs in real-time
docker logs -f kafka-streams-demo
```

### 2. Important Log Patterns

#### State Transitions
```log
State transition from REBALANCING to RUNNING
```
- Normal startup sequence
- Watch for repeated transitions

#### Processing Status
```log
Processed 1000 records with 582 iterations
```
- Monitor processing rates
- Check for stalls

#### Error Patterns
```log
ERROR [Stream Thread] Error processing record
```
- Track error rates
- Identify patterns

## Performance Tuning

### 1. JVM Tuning
```bash
KAFKA_OPTS="-Xmx2G -Xms2G -XX:+UseG1GC" ./gradlew bootRun
```

### 2. Kafka Streams Configuration
```properties
# Processing tuning
processing.guarantee=exactly_once
num.stream.threads=4
cache.max.bytes.buffering=10485760

# State store tuning
state.cleanup.delay.ms=600000
commit.interval.ms=30000
```

### 3. RocksDB Tuning
```java
Materialized.<String, ProductStats, WindowStore<Bytes, byte[]>>as("store")
    .withCachingEnabled()
    .withLoggingEnabled(Map.of(
        "retention.ms", "86400000",
        "cleanup.policy", "compact,delete"
    ));
```

## Maintenance Tasks

### 1. Regular Maintenance
```bash
# Compact topics
docker exec broker kafka-topics --bootstrap-server localhost:9092 \
    --alter --topic product-analytics \
    --config cleanup.policy=compact

# Delete old records
docker exec broker kafka-topics --bootstrap-server localhost:9092 \
    --alter --topic product-events \
    --config retention.ms=604800000
```

### 2. Backup Procedures
```bash
# Backup state stores
./gradlew backupStateStores

# Backup topics
./scripts/backup-topics.sh
```

## Next Steps

- Review the [Troubleshooting Guide](troubleshooting.md) for common issues
- Check [Examples](examples/index.md) for advanced usage
- See [Architecture](architecture.md) for system design details 