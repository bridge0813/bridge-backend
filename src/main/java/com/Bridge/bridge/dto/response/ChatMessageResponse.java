package com.Bridge.bridge.dto.response;

import com.Bridge.bridge.domain.Message;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class ChatMessageResponse {

    private String messageId;        // 채팅 메세지 ID

    private String type;             // 채팅 타입

    private String content;          //채팅 내용

    private Long senderId;           //모집자인지, 지원자인지 구분

    private LocalDateTime sendTime;  // 메세지 보낸 날짜

    private boolean readStat;        // 메세지 읽음 여부

    public ChatMessageResponse(Message message) {
        this.messageId = message.getMessageUuId();
        this.type = message.getType();
        this.content = message.getContent();
        this.senderId = message.getWriterId();
        this.sendTime = message.getSendDateTime();
        this.readStat = message.isReadStat();
    }
}
