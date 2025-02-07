package kafkastreams.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import kafkastreams.models.ProductEvent;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class ProductEventSerde implements Serde<ProductEvent> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public void close() {
    }

    @Override
    public Serializer<ProductEvent> serializer() {
        return new Serializer<>() {
            @Override
            public void configure(Map<String, ?> configs, boolean isKey) {
            }

            @Override
            public byte[] serialize(String topic, ProductEvent data) {
                try {
                    return mapper.writeValueAsBytes(data);
                } catch (Exception e) {
                    throw new RuntimeException("Error serializing ProductEvent", e);
                }
            }

            @Override
            public void close() {
            }
        };
    }

    @Override
    public Deserializer<ProductEvent> deserializer() {
        return new Deserializer<>() {
            @Override
            public void configure(Map<String, ?> configs, boolean isKey) {
            }

            @Override
            public ProductEvent deserialize(String topic, byte[] data) {
                try {
                    return mapper.readValue(data, ProductEvent.class);
                } catch (Exception e) {
                    throw new RuntimeException("Error deserializing ProductEvent", e);
                }
            }

            @Override
            public void close() {
            }
        };
    }
} 