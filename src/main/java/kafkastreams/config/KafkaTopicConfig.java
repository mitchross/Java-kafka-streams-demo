package kafkastreams.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Configuration
public class KafkaTopicConfig {

    public static final String PRODUCT_EVENTS_TOPIC = "product-events";
    public static final String PRODUCT_METADATA_TOPIC = "product-metadata";
    public static final String PRODUCT_ANALYTICS_TOPIC = "product-analytics";

    private static final int NUM_PARTITIONS = 4;
    private static final short REPLICATION_FACTOR = 1;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put("bootstrap.servers", "localhost:9092");
        return new KafkaAdmin(configs);
    }

    @Bean
    public AdminClient adminClient(KafkaAdmin kafkaAdmin) {
        return AdminClient.create(kafkaAdmin.getConfigurationProperties());
    }

    @Bean
    public List<NewTopic> createTopics(AdminClient adminClient) throws ExecutionException, InterruptedException {
        List<NewTopic> topics = List.of(
            new NewTopic(PRODUCT_EVENTS_TOPIC, NUM_PARTITIONS, REPLICATION_FACTOR),
            new NewTopic(PRODUCT_METADATA_TOPIC, NUM_PARTITIONS, REPLICATION_FACTOR),
            new NewTopic(PRODUCT_ANALYTICS_TOPIC, NUM_PARTITIONS, REPLICATION_FACTOR)
        );

        if (adminClient.listTopics().names().get().stream()
                .noneMatch(topic -> topic.equals(PRODUCT_EVENTS_TOPIC))) {
            adminClient.createTopics(topics).all().get();
        }

        return topics;
    }
} 