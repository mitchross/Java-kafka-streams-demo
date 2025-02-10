package kafkastreams;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import kafkastreams.config.KafkaTopicConfig;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

public class TestDataGenerator {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Random random = new Random();
    private static final String[] PRODUCTS = {
        "P123", "P124", "P125", "P126", "P127"
    };

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            // Generate and send events
            for (int i = 0; i < 100; i++) {
                String productId = PRODUCTS[random.nextInt(PRODUCTS.length)];
                String userId = "U" + (random.nextInt(20) + 1); // 20 different users
                String eventType = random.nextDouble() < 0.8 ? "VIEW" : "PURCHASE";
                double value = random.nextDouble() * 500; // Random value up to 500

                String json = String.format(
                    "{\"productId\":\"%s\",\"userId\":\"%s\",\"eventType\":\"%s\",\"timestamp\":%d,\"value\":%.2f}",
                    productId, userId, eventType, System.currentTimeMillis(), value
                );

                ProducerRecord<String, String> record = new ProducerRecord<>(
                    KafkaTopicConfig.PRODUCT_EVENTS_TOPIC,
                    productId,
                    json
                );

                producer.send(record);
                System.out.println("Sent event: " + json);

                Thread.sleep(100); // Small delay between events
            }
        }
    }

    static class ProductEvent {
        private String productId;
        private String userId;
        private String eventType;
        private long timestamp;
        private double value;

        // Getters and setters
        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public double getValue() { return value; }
        public void setValue(double value) { this.value = value; }
    }
}