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

    private String content;         // 메세지 내용

    private String writer;          // 메세지 보낸 사람

    private LocalDate sendDate;     // 메세지 보낸 날짜

    private LocalTime sendTime;     // 메세지 보낸 시간

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File file;              // 첨부 파일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;              // 해당 메세지가 포함된 채팅방

    @Builder
    public Message(String content, String writer, LocalDate sendDate, LocalTime sendTime, Chat chat) {
        this.content = content;
        this.writer = writer;
        this.sendDate = sendDate;
        this.sendTime = sendTime;
        this.chat = chat;
    }
}
