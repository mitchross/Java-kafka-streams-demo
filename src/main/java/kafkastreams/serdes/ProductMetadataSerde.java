package kafkastreams.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import kafkastreams.models.ProductMetadata;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class ProductMetadataSerde implements Serde<ProductMetadata> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public void close() {
    }

    @Override
    public Serializer<ProductMetadata> serializer() {
        return new Serializer<>() {
            @Override
            public void configure(Map<String, ?> configs, boolean isKey) {
            }

            @Override
            public byte[] serialize(String topic, ProductMetadata data) {
                try {
                    return mapper.writeValueAsBytes(data);
                } catch (Exception e) {
                    throw new RuntimeException("Error serializing ProductMetadata", e);
                }
            }

            @Override
            public void close() {
            }
        };
    }

    @Override
    public Deserializer<ProductMetadata> deserializer() {
        return new Deserializer<>() {
            @Override
            public void configure(Map<String, ?> configs, boolean isKey) {
            }

            @Override
            public ProductMetadata deserialize(String topic, byte[] data) {
                try {
                    return mapper.readValue(data, ProductMetadata.class);
                } catch (Exception e) {
                    throw new RuntimeException("Error deserializing ProductMetadata", e);
                }
            }

            @Override
            public void close() {
            }
        };
    }
} 