package com.Bridge.bridge.security;


import com.Bridge.bridge.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChannelInBoundInterceptor implements ChannelInterceptor {

    private final ChatService chatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.info("command = {}", accessor.getCommand());
        log.info("destination = {}", accessor.getDestination());
        log.info("message = {}", accessor.getMessage());

        handleMessage(accessor.getCommand(), accessor);

        log.info("Handle 완료");
        return message;
    }

    private void handleMessage(StompCommand command, StompHeaderAccessor accessor) {
        //구독 시 (채팅방 입장)
        if(command == StompCommand.SUBSCRIBE) {
            handleSubscribe(accessor);
        }
        //구독 취소 시 (채팅방 나가기)
        if (command == StompCommand.UNSUBSCRIBE) {
            handleUnsubscribe(accessor);
        }
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        //채팅방 가져오기
        log.info("ID = {}", accessor.getId());
        log.info("Message = {}", accessor.getMessage());

        // 채팅방 ID 와 유저 ID 분리
        String chatRoomId = getChatRoomId(accessor.getMessage());
        String userId = getUserId(accessor.getMessage());

        //입장 처리 -> 현재 접속 인원 +1
        boolean connectStat = chatService.changeConnectStat(chatRoomId);
        log.info("현 채팅방 인원 Connect State = {}", connectStat);

        //안읽은 메세지 존재시 읽음 처리
        chatService.readNotReadMessage(chatRoomId, userId);

        // 현재 접속중인 사람 있는지 체크 -> 있다면 메세지 상태 업데이트 해줘야 함
        if (connectStat == false) {
            log.info("두명다 접속중");
            chatService.updateChatHistory(chatRoomId);
        }
    }

    private void handleUnsubscribe(StompHeaderAccessor accessor) {
        log.info("ID = {}", accessor.getId());
        log.info("Message = {}", accessor.getMessage());

        // 퇴장 처리 -> 현재 접속 인원 -1
        boolean connectStat = chatService.changeConnectStat(accessor.getMessage());

        log.info("현 채팅방 인원 connect State = {}", connectStat);
        log.info("구독 취소 됌");
    }

    private String getChatRoomId(String message) {
        return message.substring(0, 36);
    }

    private String getUserId(String message) {
        return message.substring(37);
    }
}
