package com.Bridge.bridge.config.kafka;

import com.Bridge.bridge.dto.request.ChatMessageRequest;

import com.Bridge.bridge.util.Constant;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

@EnableKafka
@Configuration
public class ConsumerConfig {

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, ChatMessageRequest> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ChatMessageRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, ChatMessageRequest> consumerFactory() {
        JsonDeserializer<ChatMessageRequest> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("*");

        Map<String, Object> consumerConfigurations =
                ImmutableMap.<String, Object>builder()
                        .put(BOOTSTRAP_SERVERS_CONFIG, Constant.BOOTSTRAP_SERVER)
                        .put(GROUP_ID_CONFIG, Constant.GROUP_ID)
                        .put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                        .put(VALUE_DESERIALIZER_CLASS_CONFIG, deserializer)
                        .put(AUTO_OFFSET_RESET_CONFIG, "latest")
                        .build();

        return new DefaultKafkaConsumerFactory<>(consumerConfigurations, new StringDeserializer(), deserializer);
    }
}
