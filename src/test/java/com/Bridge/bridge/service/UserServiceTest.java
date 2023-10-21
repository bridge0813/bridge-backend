package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Field;
import com.Bridge.bridge.domain.Platform;
import com.Bridge.bridge.domain.Profile;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.UserFieldRequest;
import com.Bridge.bridge.dto.request.UserProfileRequest;
import com.Bridge.bridge.dto.response.UserProfileResponse;
import com.Bridge.bridge.exception.notfound.NotFoundProfileException;
import com.Bridge.bridge.exception.notfound.NotFoundUserException;
import com.Bridge.bridge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    @DisplayName("처음 로그인 시 개인 관심분야 등록")
    void registerField() {
        //given
        User newUser = new User("bridge", "kyukyu@apple.com", Platform.APPLE, "3d");
        User saveUser = userRepository.save(newUser);

        List<String> fields = new ArrayList<>();
        fields.add("backend");
        fields.add("frontend");
        fields.add("designer");

        UserFieldRequest request = new UserFieldRequest(saveUser.getId(), fields);

        //when
        userService.saveField(request);

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
        User newUser = new User("bridge", "kyukyu@apple.com", Platform.APPLE, "3d");
        User saveUser = userRepository.save(newUser);

        List<String> fields = new ArrayList<>();

        UserFieldRequest request = new UserFieldRequest(saveUser.getId(), fields);

        //when
        userService.saveField(request);

        //then
        User user = userRepository.findAll().get(0);
        assertEquals(0, user.getFields().size());
    }

    @Test
    @DisplayName("처음 로그인 시 개인 관심분야 등록 - 예외 반환")
    void registerFieldEX() {
        //given
        User newUser = new User("bridge", "kyukyu@apple.com", Platform.APPLE, "3d");
        User saveUser = userRepository.save(newUser);

        List<String> fields = new ArrayList<>();
        fields.add("backend");
        fields.add("frontend");
        fields.add("designer");

        UserFieldRequest request = new UserFieldRequest(saveUser.getId()+1L, fields);

        //expected
        assertThrows(NotFoundUserException.class, () -> userService.saveField(request));
    }


    @Test
    @Transactional
    @DisplayName("처음 로그인 시 개인 프로필 등록")
    void registerProfile() {
        //given
        User newUser = new User("bridge", "kyukyu@apple.com", Platform.APPLE, "3d");
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
        User newUser = new User("bridge", "kyukyu@apple.com", Platform.APPLE, "3d");
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
        assertThrows(NotFoundUserException.class, () -> userService.saveProfile(saveUser.getId()+1L, request));
    }

    @Test
    @Transactional
    @DisplayName("개인 프로필 확인 - 등록되어 있는 경우")
    void getProfile() {
        //given
        User newUser = new User("bridge", "bridge@apple.com", Platform.APPLE, "test");

        Field field = new Field("backend");

        List<String> skills = new ArrayList<>();
        skills.add("spring");
        skills.add("redis");

        Profile profile = new Profile("testLink", "selfIntro", "career", skills);

        newUser.getFields().add(field);
        newUser.updateProfile(profile);

        User saveUser = userRepository.save(newUser);

        //when
        UserProfileResponse profileResponse = userService.getProfile(saveUser.getId());

        //then
        assertEquals("bridge", profileResponse.getName());
        assertEquals("selfIntro", profileResponse.getSelfIntro());
        assertEquals("backend", profileResponse.getFields().get(0));
        assertEquals("spring", profileResponse.getStacks().get(0));
        assertEquals("redis", profileResponse.getStacks().get(1));
        assertEquals("career", profileResponse.getCareer());
        assertEquals("testLink", profileResponse.getRefLink());
    }

    @Test
    @DisplayName("개인 프로필 확인 - 예외반환")
    void getProfileEX() {
        //given
        User newUser = new User("bridge", "bridge@apple.com", Platform.APPLE, "test");
        User saveUser = userRepository.save(newUser);

        //expected
        assertThrows(NotFoundProfileException.class, () -> userService.getProfile(saveUser.getId()));
    }
}