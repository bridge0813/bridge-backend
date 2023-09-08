package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.UserFieldRequest;
import com.Bridge.bridge.dto.request.UserProfileRequest;
import com.Bridge.bridge.dto.request.UserRegisterRequest;
import com.Bridge.bridge.dto.request.UserSignUpRequest;
import com.Bridge.bridge.dto.response.UserSignUpResponse;
import com.Bridge.bridge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("처음 로그인 시 이름 정보 등록")
    void registerName() {
        //given
        User newUser = new User("kyukyu@apple.com", "3d");
        userRepository.save(newUser);

        UserSignUpRequest request = new UserSignUpRequest("3d", "브릿지");

        //when
        UserSignUpResponse response = userService.signUpName(request);

        //then
        assertEquals(1L, response.getUserId());
        User user = userRepository.findAll().get(0);
        assertEquals("브릿지", user.getName());
    }

    @Test
    @DisplayName("처음 로그인 시 이름 정보 등록 - 예외 반환")
    void registerNameEX() {
        //given
        User newUser = new User("kyukyu@apple.com", "3d");
        userRepository.save(newUser);

        UserSignUpRequest request = new UserSignUpRequest("3", "브릿지");

        //expected
        assertThrows(EntityNotFoundException.class, () -> userService.signUpName(request));
    }

    @Test
    @Transactional
    @DisplayName("처음 로그인 시 개인 관심분야 등록")
    void registerField() {
        //given
        User newUser = new User("kyukyu@apple.com", "3d");
        User saveUser = userRepository.save(newUser);

        List<String> fields = new ArrayList<>();
        fields.add("backend");
        fields.add("frontend");
        fields.add("designer");

        UserFieldRequest request = new UserFieldRequest(fields);

        //when
        userService.saveField(saveUser.getId(), request);

        //then
        User user = userRepository.findAll().get(0);
        assertEquals(3, user.getFields().size());
        assertEquals("frontend", user.getFields().get(1).getFieldName());
    }

    @Test
    @Transactional
    @DisplayName("처음 로그인 시 개인 관심분야 등록 - 아무것도 등록 안하는 경우")
    void registerFieldEmpty() {
        //given
        User newUser = new User("kyukyu@apple.com", "3d");
        User saveUser = userRepository.save(newUser);

        List<String> fields = new ArrayList<>();

        UserFieldRequest request = new UserFieldRequest(fields);

        //when
        userService.saveField(saveUser.getId(), request);

        //then
        User user = userRepository.findAll().get(0);
        assertEquals(0, user.getFields().size());
    }

    @Test
    @DisplayName("처음 로그인 시 개인 관심분야 등록 - 예외 반환")
    void registerFieldEX() {
        //given
        User newUser = new User("kyukyu@apple.com", "3d");
        User saveUser = userRepository.save(newUser);

        List<String> fields = new ArrayList<>();
        fields.add("backend");
        fields.add("frontend");
        fields.add("designer");

        UserFieldRequest request = new UserFieldRequest(fields);

        //expected
        assertThrows(EntityNotFoundException.class, () -> userService.saveField(saveUser.getId() + 1L, request));
    }


    @Test
    @Transactional
    @DisplayName("처음 로그인 시 개인 프로필 등록")
    void registerProfile() {
        //given
        User newUser = new User("kyukyu@apple.com", "3d");
        User saveUser = userRepository.save(newUser);

        List<String> stack = new ArrayList<>();
        stack.add("Spring");
        stack.add("Java");
        stack.add("Jpa");

        UserProfileRequest request = UserProfileRequest.builder()
                .selfIntro("자기 소개서")
                .career("대학생")
                .stack(stack)
                .build();

        //when
        userService.saveProfile(saveUser.getId(), request);

        //then
        User user = userRepository.findAll().get(0);
        assertEquals("자기 소개서", user.getProfile().getSelfIntro());
        assertEquals("대학생", user.getProfile().getCareer());
        assertEquals(3, user.getProfile().getSkill().size());
        assertEquals("Java", user.getProfile().getSkill().get(1));
    }

    @Test
    @DisplayName("처음 로그인 시 개인 프로필 등록 - 예외반환")
    void registerProfileEX() {
        //given
        User newUser = new User("kyukyu@apple.com", "3d");
        User saveUser = userRepository.save(newUser);

        List<String> stack = new ArrayList<>();
        stack.add("Spring");
        stack.add("Java");
        stack.add("Jpa");

        UserProfileRequest request = UserProfileRequest.builder()
                .selfIntro("자기 소개서")
                .career("대학생")
                .stack(stack)
                .build();

        //expected
        assertThrows(EntityNotFoundException.class, () -> userService.saveProfile(saveUser.getId()+1L, request));
    }

    @Test
    @Transactional
    @DisplayName("유저 등록")
    void register() {
        //given
        User newUser = new User("kyukyu@apple.com", "3d");
        newUser.registerName("bridge");
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

        //when
        userService.signUpInfo(request);

        //then
        User user = userRepository.findAll().get(0);
        assertEquals(3, user.getProfile().getSkill().size());
        assertEquals(3, user.getFields().size());
    }

    @Test
    @DisplayName("유저 등록 시 필드가 빈값인 경우")
    void registerFieldNull() {
        //given
        User newUser = new User("kyukyu@apple.com", "3d");
        newUser.registerName("bridge");
        User saveUser = userRepository.save(newUser);

        UserFieldRequest field = null;

        UserRegisterRequest request = UserRegisterRequest.builder()
                .userId(saveUser.getId())
                .userField(field)
                .build();

        //when
        boolean b = userService.saveField(request.getUserId(), request.getUserField());

        //then
        assertEquals(false, b);
    }
    @Test
    @DisplayName("유저 등록 시 프로필이 빈 값인 경우")
    void registerProfileNull() {
        //given
        User newUser = new User("kyukyu@apple.com", "3d");
        newUser.registerName("bridge");
        User saveUser = userRepository.save(newUser);

        UserProfileRequest profile = null;

        UserRegisterRequest request = UserRegisterRequest.builder()
                .userId(saveUser.getId())
                .userProfile(profile)
                .build();

        //when
        boolean b = userService.saveProfile(request.getUserId(), request.getUserProfile());

        //then
        assertEquals(false, b);
    }
}