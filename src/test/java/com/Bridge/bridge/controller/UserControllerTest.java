package com.Bridge.bridge.controller;

import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.UserFieldRequest;
import com.Bridge.bridge.dto.request.UserProfileRequest;
import com.Bridge.bridge.dto.request.UserRegisterRequest;
import com.Bridge.bridge.dto.request.UserSignUpRequest;
import com.Bridge.bridge.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원 가입시 유저 이름 등록")
    void registerName() throws Exception {
        //given
        User newUser = new User("bridge@apple.com", "3d");
        userRepository.save(newUser);

        UserSignUpRequest request = new UserSignUpRequest("3d", "브릿지");

        mockMvc.perform(post("/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 가입시 관심 분야 및 프로필 등록")
    void registerField() throws Exception {
        //given
        User newUser = new User("bridge@apple.com", "3d");
        User saveUser = userRepository.save(newUser);

        List<String> stack = new ArrayList<>();
        stack.add("Spring");
        stack.add("Java");
        stack.add("Jpa");

        UserProfileRequest profile = UserProfileRequest.builder()
                .selfIntro("자기 소개서")
                .career("대학생")
                .stack(stack)
                .build();

        List<String> fields = new ArrayList<>();
        fields.add("backend");
        fields.add("frontend");
        fields.add("designer");

        UserFieldRequest field = new UserFieldRequest(fields);

        UserRegisterRequest request = new UserRegisterRequest(saveUser.getId(), field, profile);


        mockMvc.perform(post("/signup/info")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}