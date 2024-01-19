package com.Bridge.bridge.controller;

import com.Bridge.bridge.domain.Chat;
import com.Bridge.bridge.domain.Message;
import com.Bridge.bridge.domain.Platform;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.ChatRoomRequest;
import com.Bridge.bridge.repository.ChatRepository;
import com.Bridge.bridge.repository.UserRepository;
import com.Bridge.bridge.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private JwtTokenProvider provider;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
        chatRepository.deleteAll();
    }


    @Test
    @DisplayName("채팅방 개설")
    void createChat() throws Exception {
        //given
        User user1 = new User("bridge", Platform.APPLE, "11");
        User user2 = new User("bridge2", Platform.APPLE, "12");

        User saveUser1 = userRepository.save(user1);
        User saveUser2 = userRepository.save(user2);

        ChatRoomRequest request = new ChatRoomRequest();
        request.setMakeUserId(saveUser1.getId());
        request.setReceiveUserId(saveUser2.getId());

        //expected
        mockMvc.perform(post("/chat")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("채팅방 목록 조회")
    void getChatList() throws Exception {
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

        String accessToken = provider.createAccessToken(saveUser1.getId());

        //expected
        mockMvc.perform(get("/chat/{userId}", saveUser1.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roomName").value("bridge2"))
                .andExpect(jsonPath("$[1].roomName").value("bridge3"))
                .andDo(print());
    }

    @Test
    @DisplayName("채팅방 조회")
    @Transactional
    void getChat() throws Exception {
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
                .writerId(user1.getId())
                .sendDateTime(LocalDateTime.now().withNano(0))
                .build();

        Message message2 = Message.builder()
                .content("content2")
                .writerId(user2.getId())
                .sendDateTime(LocalDateTime.now().withNano(0))
                .build();

        room1.getMessages().add(message1);
        room1.getMessages().add(message2);

        chatRepository.save(room1);

        //expected
        mockMvc.perform(get("/chat")
                        .param("chatRoomId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].senderType").value("MAKER"))
                .andExpect(jsonPath("$[1].senderType").value("APPLIER"))
                .andDo(print());
    }

    @Test
    @DisplayName("채팅방 삭제")
    void delete() throws Exception {
        //given
        Chat room1 = Chat.builder()
                .chatRoomId("1")
                .build();

        chatRepository.save(room1);

        //expected
        mockMvc.perform(MockMvcRequestBuilders.delete("/chat")
                        .param("chatRoomId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

}