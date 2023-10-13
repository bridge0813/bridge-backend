package com.Bridge.bridge.repository;

import com.Bridge.bridge.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findByChatRoomId(String chatRoomId);     // 채팅방 이이디로 채팅방 찾기

}
