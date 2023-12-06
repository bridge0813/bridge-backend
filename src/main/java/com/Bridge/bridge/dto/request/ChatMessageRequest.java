package com.Bridge.bridge.dto.request;

import com.Bridge.bridge.dto.response.ChatHistoryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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

    private ChatHistoryResponse chatHistory;

    @Builder
    public ChatMessageRequest(String messageId, String chatRoomId, MessageType type, Long senderId,
                              String message, boolean readStat, LocalDateTime sendTime, ChatHistoryResponse chatHistory) {
        this.messageId = messageId;
        this.chatRoomId = chatRoomId;
        this.type = type;
        this.senderId = senderId;
        this.message = message;
        this.readStat = readStat;
        this.sendTime = sendTime;
        this.chatHistory = chatHistory;
    }
}
