package com.Bridge.bridge.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    private String messageUuId;       // 클라이언트에서 생성한 매새자 고유 ID

    private String content;         // 메세지 내용

    private Long writerId;          // 메세지 보낸 사람 ID

    private LocalDateTime sendDateTime;     // 메세지 보낸 날짜 + 시간

    private boolean readStat;   // 읽음 여부

    private String type;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File file;              // 첨부 파일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;              // 해당 메세지가 포함된 채팅방

    @Builder
    public Message(String messageUuId, String content, Long writerId, LocalDateTime sendDateTime,boolean readStat, String type, Chat chat) {
        this.messageUuId = messageUuId;
        this.content = content;
        this.writerId = writerId;
        this.sendDateTime = sendDateTime;
        this.readStat = readStat;
        this.type = type;
        this.chat = chat;
    }

    public void changeReadStat() {
        this.readStat = true;
    }

}
