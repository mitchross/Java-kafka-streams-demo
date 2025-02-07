package kafkastreams.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductMetadata {
    private String productId;
    private String name;
    private String category;
    private double basePrice;
} 