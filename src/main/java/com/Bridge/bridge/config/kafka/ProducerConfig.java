package com.Bridge.bridge.config.kafka;

import com.Bridge.bridge.dto.request.ChatMessageRequest;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@EnableKafka
@Configuration
public class ProducerConfig {

    @Bean
    public ProducerFactory<String, ChatMessageRequest> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigurations());
    }

    @Bean
    public Map<String, Object> producerConfigurations() {
        return ImmutableMap.<String, Object>builder()
                .put(BOOTSTRAP_SERVERS_CONFIG, "54.180.195.17:9092")
                .put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
                .put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class)
                .build();
    }

    @Bean
    public KafkaTemplate<String, ChatMessageRequest> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
