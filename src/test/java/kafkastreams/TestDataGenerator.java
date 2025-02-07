package kafkastreams;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
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
            long startTime = System.currentTimeMillis();
            int messageCount = 100000; // 100K messages

            for (int i = 0; i < messageCount; i++) {
                ProductEvent event = new ProductEvent();
                event.setProductId(PRODUCTS[random.nextInt(PRODUCTS.length)]);
                event.setUserId("user" + random.nextInt(1000)); // 1000 distinct users
                event.setEventType("view");
                event.setTimestamp(System.currentTimeMillis());
                event.setValue(random.nextDouble() * 100); // Random value 0-100

                String json = mapper.writeValueAsString(event);
                producer.send(new ProducerRecord<>("product-events", event.getProductId(), json));

                if (i % 10000 == 0) { // Progress update every 10K messages
                    System.out.printf("Sent %d messages%n", i);
                }
            }

            producer.flush();
            long endTime = System.currentTimeMillis();
            System.out.printf("Sent %d messages in %d ms%n", messageCount, (endTime - startTime));
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