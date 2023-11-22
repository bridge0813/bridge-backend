package com.Bridge.bridge.security;


import com.Bridge.bridge.dto.response.ChatMessageResponse;
import com.Bridge.bridge.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChannelInBoundInterceptor implements ChannelInterceptor {

    private final ChatService chatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        System.out.println("command : "+accessor.getCommand());
        System.out.println("destination : "+accessor.getDestination());
        List<ChatMessageResponse> chatList = handleMessage(accessor.getCommand(), accessor);
        System.out.println("chatList :"  + chatList);
        if (chatList != null) {
            new SimpMessagingTemplate(channel).convertAndSend("/sub/chat/room" + accessor.getMessage(), chatList);
            System.out.println("@@@@전송 완료@@@@");
        }
        return message;
    }

    private List<ChatMessageResponse> handleMessage(StompCommand command, StompHeaderAccessor accessor) {
        //구독
        if(command == StompCommand.SUBSCRIBE) {
            //채팅방 가져오기
            log.info("ID = {}", accessor.getId());
            log.info("Message = {}", accessor.getMessage());

            System.out.println("함수 정상 작동");
            // 채팅방 ID 와 유저 ID 분리
            String chatRoomId = accessor.getMessage().substring(0, 36);
            String userId = accessor.getMessage().substring(37);

            //입장 처리 -> 현재 접속 인원 +1
            boolean connectStat = chatService.changeConnectStat(chatRoomId);
            log.info("Connect State = {}", connectStat);

            //안읽은 메세지 존재시 읽음 처리
            chatService.readNotReadMessage(chatRoomId, userId);

            // 현재 접속중인 사람 있는지 체크 -> 있다면 메세지 상태 업데이트 해줘야 함...
            if (connectStat == false) {
                System.out.println("두명다 접속중");
                return chatService.findChat(accessor.getMessage());
            }
        }
        //구독 취소
        if (command == StompCommand.UNSUBSCRIBE) {
            log.info("ID = {}", accessor.getId());
            log.info("Message = {}", accessor.getMessage());

            boolean connectStat = chatService.changeConnectStat(accessor.getMessage());
            log.info("Connect State = {}", connectStat);
            log.info("구독 취소 됌");
        }
        return null;
    }
}
