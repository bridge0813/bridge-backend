package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Chat;
import com.Bridge.bridge.domain.Message;
import com.Bridge.bridge.domain.Platform;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.ChatMessageRequest;
import com.Bridge.bridge.dto.request.ChatRoomRequest;
import com.Bridge.bridge.dto.response.ChatListResponse;
import com.Bridge.bridge.dto.response.ChatMessageResponse;
import com.Bridge.bridge.dto.response.ChatRoomResponse;
import com.Bridge.bridge.repository.ChatRepository;
import com.Bridge.bridge.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ChatServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatRepository chatRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
        chatRepository.deleteAll();
    }

    @Test
    @DisplayName("채팅방 개설")
    void createRoom() {
        //given
        User user1 = new User("bridge", "bridge@apple.com", Platform.APPLE, "11");
        User user2 = new User("bridge2", "bridge2@apple.com", Platform.APPLE, "12");

        User saveUser1 = userRepository.save(user1);
        User saveUser2 = userRepository.save(user2);

        ChatRoomRequest request = new ChatRoomRequest();
        request.setMakeUserId(saveUser1.getId());
        request.setReceiveUserId(saveUser2.getId());

        //when
        ChatRoomResponse chatRoomResponse = chatService.createChat(request);

        //then
        assertEquals(1L, chatRepository.count());
        Chat chat = chatRepository.findAll().get(0);
        assertNotNull(chatRoomResponse.getChatRoomId());
    }

    @Test
    @DisplayName("채팅방 목록 조회")
    void findAllChat() {
        //given
        User user1 = new User("bridge", "bridge@apple.com", Platform.APPLE, "11");
        User user2 = new User("bridge2", "bridge2@apple.com", Platform.APPLE, "12");
        User user3 = new User("bridge3", "bridge3@apple.com", Platform.APPLE, "13");

        User saveUser1 = userRepository.save(user1);
        User saveUser2 = userRepository.save(user2);
        User saveUser3 = userRepository.save(user3);

        Chat room1 = Chat.builder()
                .chatRoomId("1")
                .build();

        Chat room2 = Chat.builder()
                .chatRoomId("2")
                .build();

        room1.setChatUser(saveUser1, saveUser2);
        room2.setChatUser(saveUser3, saveUser1);
        chatRepository.save(room1);
        chatRepository.save(room2);

        //when
        List<ChatListResponse> allChat = chatService.findAllChat(saveUser1.getId());

        //then
        assertEquals("1", allChat.get(0).getRoomId());
        assertEquals("2", allChat.get(1).getRoomId());
        assertEquals("bridge2", allChat.get(0).getRoomName());
        assertEquals("bridge3", allChat.get(1).getRoomName());
    }

    @Test
    @DisplayName("채팅방 조회")
    @Transactional
    void findChat() {
        //given
        User user1 = new User("bridge", "bridge@apple.com", Platform.APPLE, "11");
        User user2 = new User("bridge2", "bridge2@apple.com", Platform.APPLE, "12");

        User saveUser1 = userRepository.save(user1);
        User saveUser2 = userRepository.save(user2);

        Chat room1 = Chat.builder()
                .chatRoomId("1")
                .build();

        room1.setChatUser(saveUser1, saveUser2);

        Message message1 = Message.builder()
                .content("content1")
                .writer("bridge")
                .sendDate(LocalDate.now())
                .sendTime(LocalTime.now())
                .build();

        Message message2 = Message.builder()
                .content("content2")
                .writer("bridge2")
                .sendDate(LocalDate.now())
                .sendTime(LocalTime.now())
                .build();

        room1.getMessages().add(message1);
        room1.getMessages().add(message2);

        chatRepository.save(room1);

        //when
        List<ChatMessageResponse> messages = chatService.findChat("1");

        //then
        ChatMessageResponse response1 = messages.get(0);
        ChatMessageResponse response2 = messages.get(1);
        assertEquals("content1", response1.getContent());
        assertEquals(ChatMessageResponse.SenderType.MAKER, response1.getSenderType());
        assertEquals("content2", response2.getContent());
        assertEquals(ChatMessageResponse.SenderType.APPLIER, response2.getSenderType());
    }

    @Test
    @DisplayName("채팅방 메세지 저장")
    @Transactional
    void saveMessage() throws FirebaseMessagingException {
        //given
        Chat room1 = Chat.builder()
                .chatRoomId("1")
                .build();

        chatRepository.save(room1);

        ChatMessageRequest messageRequest = new ChatMessageRequest();
        messageRequest.setChatRoomId("1");
        messageRequest.setType(ChatMessageRequest.MessageType.TALK);
        messageRequest.setSender("bridge");
        messageRequest.setMessage("content");

        //when
        chatService.saveMessage(messageRequest);

        //then
        Chat chat = chatRepository.findAll().get(0);
        Message message = chat.getMessages().get(0);
        assertEquals("content", message.getContent());
        assertEquals("bridge", message.getWriter());
    }

    @Test
    @DisplayName("채팅방 삭제")
    void deleteChat() {
        //given
        Chat room1 = Chat.builder()
                .chatRoomId("1")
                .build();

        chatRepository.save(room1);

        //when
        boolean result = chatService.deleteChat("1");

        //then
        assertTrue(result);
    }
}