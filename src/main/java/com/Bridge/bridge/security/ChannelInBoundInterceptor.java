package com.Bridge.bridge.security;


import com.Bridge.bridge.controller.ChatController;
import com.Bridge.bridge.dto.response.ChatMessageResponse;
import com.Bridge.bridge.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompClientSupport;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChannelInBoundInterceptor implements ChannelInterceptor {

    private final ChatService chatService;

    private final ChatController chatController;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        System.out.println("command : "+accessor.getCommand());
        System.out.println("destination : "+accessor.getDestination());
        System.out.println("name header : "+accessor.getFirstNativeHeader("name"));
        List<ChatMessageResponse> chatList = handleMessage(accessor.getCommand(), accessor);
        System.out.println("chatList :"  + chatList);
        if (chatList != null) {
            new SimpMessagingTemplate(channel).convertAndSend("/sub/chat/room" + accessor.getMessage(), chatList);
            System.out.println("@@@@");
        }
        return message;
    }

    private List<ChatMessageResponse> handleMessage(StompCommand command, StompHeaderAccessor accessor) {
        if(command == StompCommand.CONNECT) {
            //현재 채팅방 인원 추가 및 파악
            //채팅방 가져오기
            //입장 처리 -> 현재 접속 인원 +1
            System.out.println("detination : "+accessor.getDestination());
            System.out.println("message : "+accessor.getMessage());
            System.out.println("왜안돼?");
            boolean connectStat = chatService.changeConnectStat(accessor.getMessage());
            System.out.println("###############"+connectStat+"##############");

            //안읽은 메세지 존재시 읽음 처리
            chatService.readNotReadMessage(accessor.getMessage(), accessor.getHost());
            // 현재 접속중인 사람 있는지 체크 -> 있다면 메세지 상태 업데이트 해줘야 함...
            if (connectStat == false) {
                System.out.println("두명다 접속중");
                return chatService.findChat(accessor.getMessage());
            }
        }
        return null;
    }
}
