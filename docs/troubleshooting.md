# Troubleshooting Guide

This guide helps you diagnose and resolve common issues in the Real-Time Product Analytics platform.

## Common Issues

### 1. Application Won't Start

#### Symptoms
- Application fails to start
- Logs show connection errors
- Health check fails

#### Possible Causes & Solutions

##### Kafka Not Running
```bash
# Check if Kafka containers are running
docker ps | grep kafka

# Start the environment if needed
docker-compose up -d
```

##### Port Conflicts
```bash
# Check for port usage
lsof -i :9092
lsof -i :8080

# Stop conflicting processes or change ports in docker-compose.yml
```

##### Insufficient Memory
```bash
# Check memory usage
docker stats

# Increase memory limits in docker-compose.yml
services:
  kafka:
    mem_limit: 2g
```

### 2. Processing Delays

#### Symptoms
- High consumer lag
- Slow analytics updates
- Increasing latency

#### Possible Causes & Solutions

##### Resource Constraints
```bash
# Check CPU usage
top -b -n 1

# Check memory usage
free -m

# Adjust resource allocation
export KAFKA_HEAP_OPTS="-Xmx2G -Xms2G"
```

##### Network Issues
```bash
# Test network connectivity
nc -zv localhost 9092

# Check network stats
netstat -an | grep 9092
```

##### Configuration Issues
```properties
# Optimize processing settings
num.stream.threads=4
cache.max.bytes.buffering=10485760
commit.interval.ms=100
```

### 3. Data Quality Issues

#### Symptoms
- Missing records
- Duplicate events
- Incorrect aggregations

#### Possible Causes & Solutions

##### Schema Mismatches
```bash
# Check schema registry
curl -X GET http://localhost:8081/subjects

# Verify schema compatibility
curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
    --data '{"schema": "{\"type\":\"record\"..."}' \
    http://localhost:8081/compatibility/subjects/product-value/versions/latest
```

##### Serialization Errors
```java
// Enable detailed serialization logging
logger.setLevel(Level.DEBUG);

// Check custom serdes implementation
public class ProductSerializer implements Serializer<Product> {
    @Override
    public byte[] serialize(String topic, Product data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            logger.error("Serialization failed", e);
            throw new SerializationException(e);
        }
    }
}
```

##### State Store Corruption
```bash
# Clean up state stores
rm -rf /tmp/kafka-streams

# Verify state store backup
./gradlew verifyStateStores
```

### 4. Performance Issues

#### Symptoms
- High CPU usage
- Memory leaks
- Slow processing

#### Possible Causes & Solutions

##### Memory Leaks
```bash
# Generate heap dump
jmap -dump:format=b,file=heap.bin <pid>

# Analyze with JProfiler or MAT
jhat heap.bin
```

##### Inefficient Queries
```java
// Optimize window operations
TimeWindows.of(Duration.ofMinutes(5))
    .advanceBy(Duration.ofMinutes(1))
    .grace(Duration.ofMinutes(1));

// Use caching appropriately
Materialized.<String, Long, WindowStore<Bytes, byte[]>>as("counts")
    .withCachingEnabled()
    .withRetention(Duration.ofHours(24));
```

##### Resource Contention
```bash
# Monitor thread states
jstack <pid>

# Check for deadlocks
jcmd <pid> Thread.print
```

### 5. Monitoring Issues

#### Symptoms
- Missing metrics
- Incorrect dashboards
- Alert failures

#### Possible Causes & Solutions

##### Metric Collection
```bash
# Verify JMX metrics
jconsole localhost:9010

# Check Prometheus endpoint
curl http://localhost:8080/actuator/prometheus
```

##### Dashboard Configuration
```yaml
# Update Grafana datasource
datasources:
  - name: Prometheus
    type: prometheus
    url: http://prometheus:9090
    access: proxy
```

##### Alert Rules
```yaml
# Verify alert conditions
alerts:
  - alert: HighConsumerLag
    expr: kafka_consumer_group_lag > 1000
    for: 5m
    labels:
      severity: warning
```

## Diagnostic Commands

### System Health
```bash
# Full system check
./scripts/health-check.sh

# Component status
./scripts/component-status.sh
```

### Log Collection
```bash
# Collect all logs
./scripts/collect-logs.sh

# Filter specific errors
grep -r "ERROR" logs/ | sort | uniq -c
```

### Performance Analysis
```bash
# Generate flame graph
./scripts/flame-graph.sh

# Analyze GC logs
jstat -gcutil <pid> 1000
```

## Recovery Procedures

### 1. State Recovery
```bash
# Backup current state
./scripts/backup-state.sh

# Clean state stores
rm -rf /tmp/kafka-streams/*

# Restore from backup
./scripts/restore-state.sh
```

### 2. Topic Recovery
```bash
# Create recovery topic
kafka-topics --create --topic recovery-topic

# Copy messages
kafka-mirror-maker --consumer.config consumer.properties \
    --producer.config producer.properties \
    --whitelist "product-events"
```

### 3. Application Reset
```bash
# Stop application
./gradlew stop

# Reset offsets
kafka-consumer-groups --bootstrap-server localhost:9092 \
    --group product-analytics --reset-offsets --to-earliest \
    --all-topics --execute

# Restart application
./gradlew bootRun
```

## Support Resources

- [Kafka Streams Documentation](https://kafka.apache.org/documentation/streams/)
- [Spring for Apache Kafka](https://docs.spring.io/spring-kafka/reference/html/)
- [Confluent Platform Documentation](https://docs.confluent.io/)
- [Community Support Forum](https://forum.confluent.io/)

## Next Steps

- Review [Monitoring Guide](monitoring.md) for proactive issue prevention
- Check [Architecture](architecture.md) for system design understanding
- See [Examples](examples/index.md) for common usage patterns 