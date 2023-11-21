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

//    public enum SenderType {
//        MAKER, APPLIER;
//    }

    private String messageId;

    private String content;

    private String senderName;  //모집자인지, 지원자인지 구분

    private LocalDateTime sendTime;  // 메세지 보낸 날짜

    private boolean readStat;       // 메세지 읽음 여부

    public ChatMessageResponse(Message message) {
        this.messageId = message.getMessageUuId();
        this.content = message.getContent();
        this.senderName = message.getWriter();
        this.sendTime = LocalDateTime.of(message.getSendDate(), message.getSendTime());
        this.readStat = message.isReadStat();
    }
}
