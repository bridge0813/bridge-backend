package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Chat;
import com.Bridge.bridge.domain.Message;
import com.Bridge.bridge.domain.Platform;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.ChatMessageRequest;
import com.Bridge.bridge.dto.request.ChatRoomRequest;
import com.Bridge.bridge.dto.response.ChatHistoryResponse;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        User user1 = new User("bridge", Platform.APPLE, "11");
        User user2 = new User("bridge2", Platform.APPLE, "12");

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
    @DisplayName("채팅방 개설 - 중복 체크")
    void createRoomDup() {
        //given
        User user1 = new User("bridge", Platform.APPLE, "11");
        User user2 = new User("bridge2", Platform.APPLE, "12");

        User makeUser = userRepository.save(user1);
        User receiveUser = userRepository.save(user2);

        ChatRoomRequest request = new ChatRoomRequest();
        request.setMakeUserId(makeUser.getId());
        request.setReceiveUserId(receiveUser.getId());

        Chat oldChat = Chat.builder()
                .chatRoomId("chatroom").build();

        oldChat.setChatUser(makeUser, receiveUser);
        chatRepository.save(oldChat);

        //when
        chatService.createChat(request);

        //then
        Chat chat = chatRepository.findAll().get(0);
        assertEquals("chatroom", chat.getChatRoomId());

    }

    @Test
    @DisplayName("채팅방 목록 조회")
    void findAllChat() {
        //given
        User user1 = new User("bridge", Platform.APPLE, "11");
        User user2 = new User("bridge2", Platform.APPLE, "12");
        User user3 = new User("bridge3", Platform.APPLE, "13");

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
        User user1 = new User("bridge", Platform.APPLE, "11");
        User user2 = new User("bridge2", Platform.APPLE, "12");

        User saveUser1 = userRepository.save(user1);
        User saveUser2 = userRepository.save(user2);

        Chat room1 = Chat.builder()
                .chatRoomId("1")
                .build();

        room1.setChatUser(saveUser1, saveUser2);

        Message message1 = Message.builder()
                .content("content1")
                .writerId(saveUser1.getId())
                .sendDateTime(LocalDateTime.now().withNano(0))
                .build();

        Message message2 = Message.builder()
                .content("content2")
                .writerId(saveUser2.getId())
                .sendDateTime(LocalDateTime.now().withNano(0))
                .build();

        room1.getMessages().add(message1);
        room1.getMessages().add(message2);

        chatRepository.save(room1);

        //when
        ChatHistoryResponse chatHistory = chatService.getChatHistory("1");

        //then
        ChatMessageResponse response1 = chatHistory.getChatHistory().get(0);
        ChatMessageResponse response2 = chatHistory.getChatHistory().get(1);
        assertEquals("content1", response1.getContent());
        assertEquals(saveUser1.getId(), response1.getSenderId());
        assertEquals("content2", response2.getContent());
        assertEquals(saveUser2.getId(), response2.getSenderId());
    }

    @Test
    @DisplayName("채팅방 조회 - 메세지 없는 경우")
    @Transactional
    void findChatNoMessage() {
        //given
        User user1 = new User("bridge", Platform.APPLE, "11");
        User user2 = new User("bridge2", Platform.APPLE, "12");

        User saveUser1 = userRepository.save(user1);
        User saveUser2 = userRepository.save(user2);

        Chat room1 = Chat.builder()
                .chatRoomId("1")
                .build();

        room1.setChatUser(saveUser1, saveUser2);
        chatRepository.save(room1);

        //when
        ChatHistoryResponse chatHistory = chatService.getChatHistory("1");

        //then
        assertTrue(chatHistory.getChatHistory().isEmpty());

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
        messageRequest.setSenderId(1L);
        messageRequest.setMessage("content");

        //when
        chatService.saveMessage(messageRequest);

        //then
        Chat chat = chatRepository.findAll().get(0);
        Message message = chat.getMessages().get(0);
        assertEquals("content", message.getContent());
        assertEquals(true, message.isReadStat());
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

    @Test
    @DisplayName("채팅방 접속 인원 변경 -> 1명")
    void getConnectStatOnePeople() {
        //given
        Chat room1 = Chat.builder()
                .chatRoomId("1")
                .build();

        chatRepository.save(room1);

        //when
        chatService.changeConnectStat("1");

        //then
        Chat findChat = chatRepository.findAll().get(0);
        assertTrue(findChat.isConnectStat());
    }

    @Test
    @DisplayName("채팅방 접속 인원 변경 -> 2명")
    void getConnectStatTwoPeople() {
        //given
        Chat room1 = Chat.builder()
                .chatRoomId("1")
                .build();

        chatRepository.save(room1);

        //when
        chatService.changeConnectStat("1");
        chatService.changeConnectStat("1");

        //then
        Chat findChat = chatRepository.findAll().get(0);
        assertFalse(findChat.isConnectStat());
    }

    @Test
    @DisplayName("안읽은 메세지 읽음 처리 - 상대방 메세지")
    @Transactional
    void readNotMessage() {
        //given
        User user1 = new User("bridge", Platform.APPLE, "11");
        User user2 = new User("bridge2", Platform.APPLE, "12");

        User saveUser1 = userRepository.save(user1);
        User saveUser2 = userRepository.save(user2);

        Chat room1 = Chat.builder()
                .chatRoomId("1")
                .build();

        room1.setChatUser(saveUser1, saveUser2);

        Message message1 = Message.builder()
                .content("content1")
                .writerId(saveUser1.getId())
                .readStat(true)
                .sendDateTime(LocalDateTime.now().withNano(0))
                .build();

        Message message2 = Message.builder()
                .content("content2")
                .writerId(saveUser1.getId())
                .readStat(false)
                .sendDateTime(LocalDateTime.now().withNano(0))
                .build();

        room1.getMessages().add(message1);
        room1.getMessages().add(message2);

        chatRepository.save(room1);

        //when
        chatService.readNotReadMessage("1", String.valueOf(saveUser2.getId()));

        //then
        Chat findChat = chatRepository.findAll().get(0);
        List<Message> messages = findChat.getMessages().stream()
                .filter(m -> m.isReadStat() == false)
                .collect(Collectors.toList());
        assertEquals(0, messages.size());
    }

    @Test
    @DisplayName("안읽은 메세지 읽음 처리 - 본인 메세지")
    @Transactional
    void readNotMessageMine() {
        //given
        User user1 = new User("bridge", Platform.APPLE, "11");
        User user2 = new User("bridge2", Platform.APPLE, "12");

        User saveUser1 = userRepository.save(user1);
        User saveUser2 = userRepository.save(user2);

        Chat room1 = Chat.builder()
                .chatRoomId("1")
                .build();

        room1.setChatUser(saveUser1, saveUser2);

        Message message1 = Message.builder()
                .content("content1")
                .writerId(saveUser1.getId())
                .readStat(true)
                .sendDateTime(LocalDateTime.now().withNano(0))
                .build();

        Message message2 = Message.builder()
                .content("content2")
                .writerId(saveUser1.getId())
                .readStat(false)
                .sendDateTime(LocalDateTime.now().withNano(0))
                .build();

        room1.getMessages().add(message1);
        room1.getMessages().add(message2);

        chatRepository.save(room1);

        //when
        chatService.readNotReadMessage("1", String.valueOf(saveUser1.getId()));

        //then
        Chat findChat = chatRepository.findAll().get(0);
        List<Message> messages = findChat.getMessages().stream()
                .filter(m -> m.isReadStat() == false)
                .collect(Collectors.toList());
        assertEquals(1, messages.size());
    }
}