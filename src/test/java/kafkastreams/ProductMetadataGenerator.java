package kafkastreams;

import com.fasterxml.jackson.databind.ObjectMapper;
import kafkastreams.models.ProductMetadata;
import kafkastreams.config.KafkaTopicConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import io.confluent.kafka.serializers.KafkaJsonSerializer;

import java.util.Properties;
import java.util.Arrays;
import java.util.List;

public class ProductMetadataGenerator {
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final List<ProductMetadata> SAMPLE_PRODUCTS = Arrays.asList(
        new ProductMetadata("P123", "Premium Headphones", "Electronics", 199.99),
        new ProductMetadata("P124", "Wireless Mouse", "Electronics", 49.99),
        new ProductMetadata("P125", "Gaming Keyboard", "Electronics", 149.99),
        new ProductMetadata("P126", "Smart Watch", "Wearables", 299.99),
        new ProductMetadata("P127", "Fitness Tracker", "Wearables", 99.99)
    );

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaJsonSerializer.class);
        props.put("schema.registry.url", "http://localhost:8081");

        try (KafkaProducer<String, ProductMetadata> producer = new KafkaProducer<>(props)) {
            for (ProductMetadata product : SAMPLE_PRODUCTS) {
                ProducerRecord<String, ProductMetadata> record = new ProducerRecord<>(
                    KafkaTopicConfig.PRODUCT_METADATA_TOPIC, 
                    product.getProductId(), 
                    product
                );
                producer.send(record);
                System.out.println("Sent metadata: " + mapper.writeValueAsString(product));
            }
            producer.flush();
            System.out.println("Successfully loaded product metadata");
        } catch (Exception e) {
            System.err.println("Error generating product metadata: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 