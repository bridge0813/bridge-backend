package com.Bridge.bridge.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "user_id", name = "make_user_id")
    private User sendUser;      // 채팅방 만든 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "user_id", name = "receive_user_id")
    private User receiveUser;   // 채팅방 참가한 사람

    @OneToMany(mappedBy = "chat")
    private List<Message> messages = new ArrayList<>();     // 해당 채팅방이 담고있는 메세지 목록

}
