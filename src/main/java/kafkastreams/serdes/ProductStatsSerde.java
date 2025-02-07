package kafkastreams.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import kafkastreams.models.ProductStats;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class ProductStatsSerde implements Serde<ProductStats> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public void close() {
    }

    @Override
    public Serializer<ProductStats> serializer() {
        return new Serializer<>() {
            @Override
            public void configure(Map<String, ?> configs, boolean isKey) {
            }

            @Override
            public byte[] serialize(String topic, ProductStats data) {
                try {
                    return mapper.writeValueAsBytes(data);
                } catch (Exception e) {
                    throw new RuntimeException("Error serializing ProductStats", e);
                }
            }

            @Override
            public void close() {
            }
        };
    }

    @Override
    public Deserializer<ProductStats> deserializer() {
        return new Deserializer<>() {
            @Override
            public void configure(Map<String, ?> configs, boolean isKey) {
            }

            @Override
            public ProductStats deserialize(String topic, byte[] data) {
                try {
                    return mapper.readValue(data, ProductStats.class);
                } catch (Exception e) {
                    throw new RuntimeException("Error deserializing ProductStats", e);
                }
            }

            @Override
            public void close() {
            }
        };
    }
} 