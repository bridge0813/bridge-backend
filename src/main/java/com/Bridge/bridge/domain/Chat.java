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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "user_id", name = "make_user_id")
    private User makeUser;      // 채팅방 만든 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "user_id", name = "receive_user_id")
    private User receiveUser;   // 채팅방 참가한 사람

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();     // 해당 채팅방이 담고있는 메세지 목록

    @Builder
    public Chat(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    //--연관관계 메소드--//
    public void setChatUser(User maker, User receiver) {
        this.makeUser = maker;
        maker.getMadeChat().add(this);

        this.receiveUser = receiver;
        receiver.getJoinChat().add(this);
    }
}
