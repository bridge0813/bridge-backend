package com.Bridge.bridge.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    private String messageId;       // 클라이언트에서 생성한 매새자 고유 ID

    private String content;         // 메세지 내용

    private String writer;          // 메세지 보낸 사람

    private LocalDate sendDate;     // 메세지 보낸 날짜

    private LocalTime sendTime;     // 메세지 보낸 시간

    private boolean readStat;   // 읽음 여부

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File file;              // 첨부 파일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;              // 해당 메세지가 포함된 채팅방

    @Builder
    public Message(String messageId, String content, String writer, LocalDate sendDate, LocalTime sendTime, Chat chat) {
        this.messageId = messageId;
        this.content = content;
        this.writer = writer;
        this.sendDate = sendDate;
        this.sendTime = sendTime;
        this.readStat = false;
        this.chat = chat;
    }

    public void changeReadStat() {
        this.readStat = true;
    }

}
