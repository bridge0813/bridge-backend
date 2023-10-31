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
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long id;

    private String type;                // 알림 타입

    private String title;               // 제목

    private String content;             // 내용

    private LocalDateTime sendDateTime;         // 발신 날짜 + 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "user_id" , name = "rcv_user_id")
    private User rcvUser;               // 수신자

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(referencedColumnName = "user_id", name = "send_user_id")
//    private User sendUser;              // 발신자

    @Builder
    public Alarm(String type, String title, String content, LocalDateTime sendDateTime, User rcvUser) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.sendDateTime = sendDateTime;
        this.rcvUser = rcvUser;
    }
}
