package com.Bridge.bridge.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest implements Serializable {

    public enum MessageType {
        TALK, ACCEPT, REJECT;
    }

    private String messageId;   // 메세지 고유 ID

    private String chatRoomId;  // 채팅방 ID

    private MessageType type;   // 메세지 타입

    private Long senderId;      // 메세지 보낸 사람 ID

    private String message;     // 메세지 내용

    private boolean readStat;   // 메세지 읽음 여부

    private LocalDateTime sendTime;     // 메세지 보낸시간
}
