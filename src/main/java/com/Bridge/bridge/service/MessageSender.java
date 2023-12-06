package com.Bridge.bridge.service;

import com.Bridge.bridge.dto.request.ChatMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageSender {

    private final KafkaTemplate<String, ChatMessageRequest> kafkaTemplate;

    public void send(String topic, ChatMessageRequest message) {

        // Kafka Template 을 사용하여 메세지를 지정된 토픽으로 전송
        kafkaTemplate.send(topic, message);
    }
}
