package com.Bridge.bridge.controller;

import com.Bridge.bridge.domain.Bookmark;
import com.Bridge.bridge.domain.Field;
import com.Bridge.bridge.domain.Part;
import com.Bridge.bridge.domain.Platform;
import com.Bridge.bridge.domain.Profile;
import com.Bridge.bridge.domain.Project;
import com.Bridge.bridge.domain.Stack;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.ProfileUpdateRequest;
import com.Bridge.bridge.dto.request.UserFieldRequest;
import com.Bridge.bridge.dto.request.UserProfileRequest;
import com.Bridge.bridge.repository.ProjectRepository;
import com.Bridge.bridge.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @DisplayName("회원 가입시 관심 분야 등록")
    void registerField() throws Exception {
        //given
        User newUser = new User("bridge","bridge@apple.com", Platform.APPLE,"3d");
        User saveUser = userRepository.save(newUser);


        List<String> fields = new ArrayList<>();
        fields.add("BACKEND");
        fields.add("FRONTEND");
        fields.add("UIUX");

        UserFieldRequest request = new UserFieldRequest(saveUser.getId(), fields);

        //expected
        mockMvc.perform(post("/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("프로필 등록")
    void createProfile() throws Exception {
        //given
        User newUser = new User("bridge", "kyukyu@apple.com", Platform.APPLE, "3d");
        User saveUser = userRepository.save(newUser);

        List<String> stack = new ArrayList<>();
        stack.add("SPRING");
        stack.add("JAVA");

        UserProfileRequest request = UserProfileRequest.builder()
                .refLink("link")
                .selfIntro("자기 소개서")
                .career("대학생")
                .stack(stack)
                .build();
        MockMultipartFile file = new MockMultipartFile("photo", "test.jpg", "image/jpg", new FileInputStream("/Users/kh/Desktop/file/테이블.jpg"));
        MockMultipartFile profile = new MockMultipartFile("profile", "profile", "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

        //expected
        mockMvc.perform(multipart("/users/profile")
                        .file(profile)
                        .file(file)
                        .param("userId", String.valueOf(saveUser.getId())))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Transactional
    @DisplayName("개인 프로필 확인")
    void getProfile() throws Exception {
        //given
        User newUser = new User("bridge", "bridge@apple.com", Platform.APPLE, "test");

        List<Stack> skills = new ArrayList<>();
        skills.add(Stack.SPRING);
        skills.add(Stack.REDIS);

        Profile profile = new Profile("testLink", "selfIntro", "career", skills);

        newUser.getFields().add(Field.BACKEND);
        newUser.updateProfile(profile);

        User saveUser = userRepository.save(newUser);

        //expected
        mockMvc.perform(get("/users/profile")
                        .param("userId", saveUser.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("bridge"))
                .andExpect(jsonPath("$.selfIntro").value("selfIntro"))
                .andExpect(jsonPath("$.fields[0]").value("백엔드"))
                .andExpect(jsonPath("$.stacks[0]").value("Spring"))
                .andExpect(jsonPath("$.career").value("career"))
                .andExpect(jsonPath("$.refLink").value("testLink"))
                .andDo(print());
    }

    @Test
    @DisplayName("프로필 수정")
    void updateProfile() throws Exception {
        //given
        User newUser = new User("bridge", "bridge@apple.com", Platform.APPLE, "test");

        List<Stack> skills = new ArrayList<>();
        skills.add(Stack.SPRING);
        skills.add(Stack.REDIS);

        Profile profile = new Profile("testLink", "selfIntro", "career", skills);

        newUser.getFields().add(Field.BACKEND);
        newUser.updateProfile(profile);

        User saveUser = userRepository.save(newUser);

        List<String> newSkills = new ArrayList<>();
        newSkills.add("MYSQL");

        ProfileUpdateRequest updateRequest = ProfileUpdateRequest.builder()
                .selfIntro("updateIntro")
                .refLink("updateLink")
                .career("updateCareer")
                .stack(newSkills)
                .build();

        MockMultipartFile request = new MockMultipartFile("request", "profile", "application/json", objectMapper.writeValueAsString(updateRequest).getBytes(StandardCharsets.UTF_8));

        //expected
        mockMvc.perform(multipart(HttpMethod.PUT, "/users/profile")
                        .file(request)
                        .param("userId", String.valueOf(saveUser.getId())))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Transactional
    @DisplayName("북마크 프로젝트 목록 조회")
    void showBookmarkProjects() throws Exception {
        //given
        User newUser = new User("bridge", "bridge@apple.com", Platform.APPLE, "test");

        List<Part> recruits = new ArrayList<>();
        recruits.add(new Part(null, 3, null, null, null));
        recruits.add(new Part(null, 2, null, null, null));
        LocalDateTime now = LocalDateTime.now();

        Project project = Project.builder()
                .title("title")
                .overview("overview")
                .dueDate(now)
                .recruit(recruits)
                .build();

        projectRepository.save(project);

        Bookmark bookmark = new Bookmark(newUser, project);

        newUser.getBookmarks().add(bookmark);
        User saveUser = userRepository.save(newUser);

        //expected
        mockMvc.perform(get("/users/bookmark")
                        .param("userId", String.valueOf(saveUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("title"))
                .andExpect(jsonPath("$[0].dueDate").value(String.valueOf(now)))
                .andExpect(jsonPath("$[0].recruitTotalNum").value(5))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 탈퇴")
    void deleteUser() throws Exception {
        //given
        User newUser = new User("bridge", "bridge@apple.com", Platform.APPLE, "test");

        Project project = Project.builder()
                .title("title")
                .overview("overview")
                .dueDate(LocalDateTime.now())
                .recruit(null)
                .user(newUser)
                .build();

        newUser.getProjects().add(project);
        User saveUser = userRepository.save(newUser);

        //expected
        mockMvc.perform(delete("/users/{userId}", saveUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("로그아웃 기능")
    void logout() throws Exception {
        //given
        User newUser = new User("bridge", "bridge@apple.com", Platform.APPLE, "test");
        newUser.updateRefreshToken("refreshToken");
        User saveUser = userRepository.save(newUser);

        //expected
        mockMvc.perform(post("/logout")
                        .param("userId", String.valueOf(saveUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("마이페이지 조회")
    void myPage() throws Exception {
        //given
        User newUser = new User("bridge", "bridge@apple.com", Platform.APPLE, "test");

        newUser.getFields().add(Field.BACKEND);
        newUser.getFields().add(Field.FRONTEND);

        Project newProject = Project.builder()
                .title("title")
                .overview("overview")
                .build();
        projectRepository.save(newProject);

        Bookmark bookmark = Bookmark.builder()
                .user(newUser)
                .project(newProject)
                .build();

        newUser.setBookmarks(bookmark);
        User saveUser = userRepository.save(newUser);

        //expected
        mockMvc.perform(get("/users/mypage")
                        .param("userId", String.valueOf(saveUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.field[0]").value("백엔드"))
                .andExpect(jsonPath("$.field[1]").value("프론트엔드"))
                .andExpect(jsonPath("$.bookmarkNum").value(1))
                .andDo(print());
    }
}