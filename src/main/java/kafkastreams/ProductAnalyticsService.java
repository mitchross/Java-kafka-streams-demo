package kafkastreams;

import jakarta.annotation.PostConstruct;
import kafkastreams.models.ProductEvent;
import kafkastreams.models.ProductMetadata;
import kafkastreams.models.ProductStats;
import kafkastreams.serdes.ProductEventSerde;
import kafkastreams.serdes.ProductMetadataSerde;
import kafkastreams.serdes.ProductStatsSerde;
import kafkastreams.config.KafkaTopicConfig;
import org.springframework.stereotype.Service;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.KafkaStreams;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.WindowedSerdes;

@Service
public class ProductAnalyticsService {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final KafkaStreams streams;
    private final KafkaStreamsConfiguration kafkaStreamsConfiguration;

    @Autowired
    public ProductAnalyticsService(KafkaStreamsConfiguration kafkaStreamsConfiguration) {
        this.kafkaStreamsConfiguration = kafkaStreamsConfiguration;
        this.streams = buildStreams();
    }

    @PostConstruct
    public void startStreams() {
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private KafkaStreams buildStreams() {
        StreamsBuilder builder = new StreamsBuilder();

        // Define time windows
        TimeWindows timeWindows = TimeWindows.of(Duration.ofMinutes(5))
                .advanceBy(Duration.ofMinutes(1));

        // Create streams from input topics
        KStream<String, String> inputStream = builder.stream(KafkaTopicConfig.PRODUCT_EVENTS_TOPIC, 
            Consumed.with(Serdes.String(), Serdes.String()));
        
        KTable<String, ProductMetadata> metadataTable = builder.table(KafkaTopicConfig.PRODUCT_METADATA_TOPIC,
            Consumed.with(Serdes.String(), new ProductMetadataSerde()));

        // Parse JSON and key by productId
        KStream<String, ProductEvent> productEvents = inputStream
                .mapValues(value -> {
                    try {
                        return mapper.readValue(value, ProductEvent.class);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter((key, value) -> value != null)
                .selectKey((key, value) -> value.getProductId());

        // Create windowed aggregation
        KTable<Windowed<String>, ProductStats> productStats = productEvents
                .groupByKey(Grouped.with(Serdes.String(), new ProductEventSerde()))
                .windowedBy(timeWindows)
                .aggregate(
                        () -> new ProductStats(),
                        (key, event, stats) -> {
                            stats.setProductId(key);
                            stats.incrementViewCount();
                            stats.addValue(event.getValue());
                            stats.setWindowStart(event.getTimestamp());
                            stats.setWindowEnd(event.getTimestamp() + timeWindows.size());
                            return stats;
                        },
                        Materialized.<String, ProductStats, WindowStore<Bytes, byte[]>>as("product-stats-store")
                            .withKeySerde(Serdes.String())
                            .withValueSerde(new ProductStatsSerde())
                );

        // Handle unique users with state store
        KStream<Windowed<String>, Long> uniqueUserStream = productEvents
                .groupBy((key, value) -> key + "-" + value.getUserId(),
                    Grouped.with(Serdes.String(), new ProductEventSerde()))
                .windowedBy(timeWindows)
                .count()
                .toStream();

        KTable<Windowed<String>, Long> uniqueUsers = uniqueUserStream
                .map((windowedKey, value) -> KeyValue.pair(
                        new Windowed<>(windowedKey.key().split("-")[0], windowedKey.window()),
                        value
                ))
                .groupByKey(Grouped.with(WindowedSerdes.timeWindowedSerdeFrom(String.class), Serdes.Long()))
                .reduce(Long::sum);

        // Combine stats with unique users and metadata
        productStats
            .join(uniqueUsers,
                (stats, unique) -> {
                    stats.setUniqueUsers(unique);
                    stats.updateConversionRate();
                    return stats;
                })
            .toStream()
            .map((windowedKey, value) -> KeyValue.pair(windowedKey.key(), value))
            .join(metadataTable,
                (stats, metadata) -> {
                    stats.setName(metadata.getName());
                    stats.setCategory(metadata.getCategory());
                    stats.setBasePrice(metadata.getBasePrice());
                    return stats;
                },
                Joined.with(Serdes.String(), new ProductStatsSerde(), new ProductMetadataSerde()))
            .to("product-analytics", Produced.with(Serdes.String(), new ProductStatsSerde()));

        return new KafkaStreams(builder.build(), kafkaStreamsConfiguration.getProperties());
    }
}