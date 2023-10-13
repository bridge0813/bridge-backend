package com.Bridge.bridge.dto.request;

import lombok.Data;

@Data
public class ChatMessageRequest {

    public enum MessageType {
        ENTER, TALK, LEAVE;
    }

    private String chatRoomId;  // 채팅방 ID

    private MessageType type;   // 메세지 타입

    private String sender;      // 메세지 보낸 사람

    private String message;     // 메세지 내용
}
