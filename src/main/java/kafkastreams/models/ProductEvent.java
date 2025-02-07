package kafkastreams.models;



import lombok.Data;

@Data
public class ProductEvent {
    private String productId;
    private String userId;
    private String eventType;
    private long timestamp;
    private double value;
}