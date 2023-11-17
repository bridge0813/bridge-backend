package com.Bridge.bridge.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageRequest {

    public enum MessageType {
        TALK, ACCEPT, REJECT;
    }

    private String messageId;   // 메세지 고유 ID

    private String chatRoomId;  // 채팅방 ID

    private MessageType type;   // 메세지 타입

    private String sender;      // 메세지 보낸 사람

    private String message;     // 메세지 내용

    private boolean readStat;   // 메세지 읽음 여부

    private LocalDateTime sendTime;     // 메세지 보낸시간
}
