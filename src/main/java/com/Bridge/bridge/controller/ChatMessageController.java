package com.Bridge.bridge.controller;

import com.Bridge.bridge.dto.request.ChatMessageRequest;
import com.Bridge.bridge.dto.response.ChatMessageResponse;
import com.Bridge.bridge.service.AlarmService;
import com.Bridge.bridge.service.ChatService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatService chatService;

    /**
     * 채팅방에 들어오는 경우
     * 모집자가 채팅방을 만듦과 동시에 지원자 모집자 둘 다 입장해야함(구독)
     */
    @MessageMapping("/chat/enter")
    public void enter(@Payload ChatMessageRequest chatMessageRequest) {

        chatMessageRequest.setMessage(chatMessageRequest.getSender() + "이 입장하셨습니다.");

        simpMessagingTemplate.convertAndSend("/sub/chat/room/" + chatMessageRequest.getChatRoomId(), chatMessageRequest);
    }

    /**
     * 채팅방에 메세지 보내는 경우됌
     */
    @MessageMapping("/chat/message")
    public void sendMessage(ChatMessageRequest chatMessageRequest) throws FirebaseMessagingException {
        log.info("message = {}", chatMessageRequest.getMessage());

        //접속 중인 인원 확인
        ChatMessageRequest messageRequest = chatService.saveMessage(chatMessageRequest);
        log.info("change message = {}", messageRequest.getMessage());
        simpMessagingTemplate.convertAndSend("/sub/chat/room/" + messageRequest.getChatRoomId(), messageRequest);
    }

    public void updateMessage(List<ChatMessageResponse> chatList, String chatRoomId) {
        simpMessagingTemplate.convertAndSend("/sub/chat/room/" + chatRoomId, chatList);
    }
}
