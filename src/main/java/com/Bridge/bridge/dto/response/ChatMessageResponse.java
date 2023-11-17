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

    public enum SenderType {
        MAKER, APPLIER;
    }

    private String content;

    private SenderType senderType;  //모집자인지, 지원자인지 구분

    private LocalDateTime sendTime;  // 메세지 보낸 날짜

    private boolean readStat;       // 메세지 읽음 여부

    public ChatMessageResponse(Message message, SenderType senderType) {
        this.content = message.getContent();
        this.senderType = senderType;
        this.sendTime = LocalDateTime.of(message.getSendDate(), message.getSendTime());
        this.readStat = message.isReadStat();
    }
}
