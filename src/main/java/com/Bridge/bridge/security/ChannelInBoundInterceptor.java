package com.Bridge.bridge.security;

import com.Bridge.bridge.domain.Chat;
import com.Bridge.bridge.dto.response.ChatMessageResponse;
import com.Bridge.bridge.repository.ChatRepository;
import com.Bridge.bridge.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChannelInBoundInterceptor implements ChannelInterceptor {

    private final ChatService chatService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        System.out.println("command : "+accessor.getCommand());
        System.out.println("destination : "+accessor.getDestination());
        System.out.println("name header : "+accessor.getFirstNativeHeader("name"));
        handleMessage(accessor.getCommand(), accessor);
        return message;
    }

    private void handleMessage(StompCommand command, StompHeaderAccessor accessor) {
        switch (command) {
            case CONNECT:
                //현재 채팅방 인원 추가 및 파악
                connectToChatRoom(accessor);
                break;
        }
    }

    private void connectToChatRoom(StompHeaderAccessor accessor) {
        //채팅방 가져오기
        //입장 처리 -> 현재 접속 인원 +1
        System.out.println("detination : "+accessor.getDestination());
        System.out.println("message : "+accessor.getMessage());
        boolean connectStat = chatService.changeConnectStat(accessor.getMessage());

        //안읽은 메세지 존재시 읽음 처리
        chatService.readNotReadMessage(accessor.getMessage());

        // 현재 접속중인 사람 있는지 체크 -> 있다면 메세지 상태 업데이트 해줘야 함...
        if (connectStat == false) {
            List<ChatMessageResponse> chatList = chatService.findChat(accessor.getMessage());
            simpMessagingTemplate.convertAndSend("/sub/chat/room/" + accessor.getMessage(), chatList);
        }
    }
}
