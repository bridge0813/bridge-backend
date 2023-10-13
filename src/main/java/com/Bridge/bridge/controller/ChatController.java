package com.Bridge.bridge.controller;

import com.Bridge.bridge.dto.request.ChatRoomRequest;
import com.Bridge.bridge.dto.response.ChatListResponse;
import com.Bridge.bridge.dto.response.ChatMessageResponse;
import com.Bridge.bridge.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    /**
     * 채팅방 개설
     */
    @PostMapping("/chat")
    public ResponseEntity<?> createChat(@RequestBody ChatRoomRequest chatRoomRequest) {
        String chatRoomId = chatService.createChat(chatRoomRequest);
        return ResponseEntity.ok(chatRoomId);
    }

    /**
     * 채팅방 목록 조회
     */
    @GetMapping("/chat/{userId}")
    public ResponseEntity<?> getChatList(@PathVariable Long userId) {
        List<ChatListResponse> allChat = chatService.findAllChat(userId);
        return ResponseEntity.ok(allChat);
    }

    /**
     * 채팅방 개별 조회
     */
    @GetMapping("/chat")
    public ResponseEntity<?> getChat(@RequestParam("chatRoomId") String chatRoomId) {
        List<ChatMessageResponse> chatMessages = chatService.findChat(chatRoomId);
        return ResponseEntity.ok(chatMessages);
    }

    /**
     * 채팅방 나가기
     */
    @DeleteMapping("/chat")
    public ResponseEntity deleteChat(@RequestParam("chatRoomId") String chatRoomId) {
        boolean result = chatService.deleteChat(chatRoomId);

        return ResponseEntity.ok(result);
    }
}
