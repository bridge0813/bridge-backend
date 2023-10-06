package com.Bridge.bridge.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

    private String chatRoomId; // 채팅방 고유 ID

    private String roomName; // 채팅방 이름

    @OneToMany(mappedBy = "chat")
    private List<Message> messages = new ArrayList<>();     // 해당 채팅방이 담고있는 메세지 목록

}
