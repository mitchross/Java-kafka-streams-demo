#!/bin/bash

echo "🧹 Cleaning up Kafka environment..."

# Stop the application if running
echo "Stopping any running application instances..."
pkill -f kafka-streams-demo

# List all topics
echo "📋 Current topics:"
docker exec broker kafka-topics --list --bootstrap-server localhost:9092

# Delete all project-related topics
echo "🗑️ Deleting project topics..."
topics=(
    "product-events"
    "product-metadata"
    "product-analytics"
    "product-analytics-KSTREAM-AGGREGATE-STATE-STORE-0000000012-repartition"
    "product-analytics-KSTREAM-REDUCE-STATE-STORE-0000000019-repartition"
    "product-analytics-product-stats-store-repartition"
    "product-analytics-KSTREAM-MAP-0000000028-repartition"
    "product-analytics-product-stats-store-changelog"
)

for topic in "${topics[@]}"; do
    echo "Deleting topic: $topic"
    docker exec broker kafka-topics --delete \
        --bootstrap-server localhost:9092 \
        --topic "$topic" 2>/dev/null || true
done

# Delete consumer groups
echo "🗑️ Deleting consumer groups..."
docker exec broker kafka-consumer-groups \
    --bootstrap-server localhost:9092 \
    --delete --group product-analytics 2>/dev/null || true

# Restart Kafka container to clean up any remaining state
echo "🔄 Restarting Kafka services..."
docker-compose down
docker-compose up -d

# Wait for Kafka to be ready
echo "⏳ Waiting for Kafka to be ready..."
sleep 30

echo "✨ Cleanup complete! Topics are now:"
docker exec broker kafka-topics --list --bootstrap-server localhost:9092

echo "
🚀 To start fresh:
1. Run './gradlew loadProductMetadata'
2. Run './gradlew bootRun'
3. Run './gradlew generateTestData'
" 