package com.Bridge.bridge.service;

import com.Bridge.bridge.dto.request.ChatMessageRequest;
import com.Bridge.bridge.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageReceiver {

    private final SimpMessagingTemplate template;

    @KafkaListener(topics = Constant.KAFKA_TOPIC, groupId = Constant.GROUP_ID,
    containerFactory = "kafkaListenerContainerFactory")
    public void receiveMessage(ChatMessageRequest message) {

        // 메세지객체 내부의 채팅방 ID 참조 -> 구독자에게 메세지 발송
        template.convertAndSend("/sub/chat/room/" + message.getChatRoomId(), message);
        log.info("클라이언트로 메세지 전송 완료");
    }
}
