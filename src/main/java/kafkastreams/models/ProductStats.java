package kafkastreams.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStats {
    private String productId;
    // Metadata fields
    private String name;
    private String category;
    private double basePrice;
    // Analytics fields
    private long viewCount;
    private long uniqueUsers;
    private double totalValue;
    private double conversionRate;
    private long windowStart;
    private long windowEnd;

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void addValue(double value) {
        this.totalValue += value;
    }

    public void updateConversionRate() {
        if (viewCount > 0) {
            this.conversionRate = (double) uniqueUsers / viewCount;
        }
    }
}