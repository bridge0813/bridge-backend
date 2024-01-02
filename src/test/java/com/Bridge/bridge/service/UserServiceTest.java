package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Bookmark;
import com.Bridge.bridge.domain.Field;
import com.Bridge.bridge.domain.File;
import com.Bridge.bridge.domain.Part;
import com.Bridge.bridge.domain.Platform;
import com.Bridge.bridge.domain.Profile;
import com.Bridge.bridge.domain.Project;
import com.Bridge.bridge.domain.Stack;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.ProfileUpdateRequest;
import com.Bridge.bridge.dto.request.UserFieldRequest;
import com.Bridge.bridge.dto.request.UserProfileRequest;
import com.Bridge.bridge.dto.response.BookmarkListResponse;
import com.Bridge.bridge.dto.response.MyPageResponse;
import com.Bridge.bridge.dto.response.UserProfileResponse;
import com.Bridge.bridge.exception.notfound.NotFoundProfileException;
import com.Bridge.bridge.exception.notfound.NotFoundUserException;
import com.Bridge.bridge.repository.FileRepository;
import com.Bridge.bridge.repository.ProjectRepository;
import com.Bridge.bridge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
        projectRepository.deleteAll();
        fileRepository.deleteAll();
    }

    @Test
    @Transactional
    @DisplayName("처음 로그인 시 개인 관심분야 등록")
    void registerField() {
        //given
        User newUser = new User("bridge", Platform.APPLE, "3d");
        User saveUser = userRepository.save(newUser);

        List<String> fields = new ArrayList<>();
        fields.add("BACKEND");
        fields.add("FRONTEND");
        fields.add("UIUX");

        UserFieldRequest request = new UserFieldRequest(saveUser.getId(), fields);

        //when
        userService.saveField(request);

        //then
        User user = userRepository.findAll().get(0);
        assertEquals(3, user.getFields().size());
        assertEquals("프론트엔드", user.getFields().get(1).getValue());
    }

    @Test
    @Transactional
    @DisplayName("처음 로그인 시 개인 관심분야 등록 - 아무것도 등록 안하는 경우")
    void registerFieldEmpty() {
        //given
        User newUser = new User("bridge", Platform.APPLE, "3d");
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
        User newUser = new User("bridge", Platform.APPLE, "3d");
        User saveUser = userRepository.save(newUser);

        List<String> fields = new ArrayList<>();
        fields.add("BACKEND");
        fields.add("FRONTEND");
        fields.add("UIUX");

        UserFieldRequest request = new UserFieldRequest(saveUser.getId()+1L, fields);

        //expected
        assertThrows(NotFoundUserException.class, () -> userService.saveField(request));
    }


    @Test
    @Transactional
    @DisplayName("개인 프로필 등록 - 파일 없는 경우")
    void registerProfile() {
        //given
        User newUser = new User("bridge", Platform.APPLE, "3d");
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

        //when
        userService.saveProfile(saveUser.getId(), request, null, null);

        //then
        User user = userRepository.findAll().get(0);
        assertEquals("자기 소개서", user.getProfile().getSelfIntro());
        assertEquals("대학생", user.getProfile().getCareer());
        assertEquals(2, user.getProfile().getSkill().size());
        assertEquals("Java", user.getProfile().getSkill().get(1).getValue());
    }

    @Test
    @Transactional
    @DisplayName("개인 프로필 등록 - 파일 있는 경우")
    void registerProfileFile() throws IOException {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpg", new FileInputStream("/Users/kh/Desktop/file/테이블.jpg"));

        User newUser = new User("bridge", Platform.APPLE, "3d");
        User saveUser = userRepository.save(newUser);

        List<String> stack = new ArrayList<>();
        stack.add("SPRING");
        stack.add("JAVA");

        UserProfileRequest request = UserProfileRequest.builder()
                .selfIntro("자기 소개서")
                .career("대학생")
                .stack(stack)
                .build();

        //when
        userService.saveProfile(saveUser.getId(), request, file, null);

        //then
        User user = userRepository.findAll().get(0);
        assertNotNull(user.getProfile().getProfilePhoto());
    }

    @Test
    @DisplayName("개인 프로필 등록 - 예외반환")
    void registerProfileEX() {
        //given
        User newUser = new User("bridge", Platform.APPLE, "3d");
        User saveUser = userRepository.save(newUser);

        List<String> stack = new ArrayList<>();
        stack.add("SPRING");
        stack.add("JAVA");

        UserProfileRequest request = UserProfileRequest.builder()
                .selfIntro("자기 소개서")
                .career("대학생")
                .stack(stack)
                .build();

        //expected
        assertThrows(NotFoundUserException.class, () -> userService.saveProfile(saveUser.getId()+1L, request, null, null));
    }

    @Test
    @Transactional
    @DisplayName("개인 프로필 확인 - 등록되어 있는 경우")
    void getProfile() throws MalformedURLException {
        //given
        User newUser = new User("bridge", Platform.APPLE, "test");

        List<Stack> skills = new ArrayList<>();
        skills.add(Stack.SPRING);
        skills.add(Stack.REDIS);

        Profile profile = new Profile("testLink", "selfIntro", "career", skills);

        newUser.getFields().add(Field.BACKEND);
        newUser.updateProfile(profile);

        User saveUser = userRepository.save(newUser);

        //when
        UserProfileResponse profileResponse = userService.getProfile(saveUser.getId());

        //then
        assertEquals("bridge", profileResponse.getName());
        assertEquals("selfIntro", profileResponse.getSelfIntro());
        assertEquals("백엔드", profileResponse.getFields().get(0));
        assertEquals("Spring", profileResponse.getStacks().get(0));
        assertEquals("Redis", profileResponse.getStacks().get(1));
        assertEquals("career", profileResponse.getCareer());
        assertEquals("testLink", profileResponse.getRefLink());
    }

    @Test
    @DisplayName("개인 프로필 확인 - 예외반환")
    void getProfileEX() {
        //given
        User newUser = new User("bridge", Platform.APPLE, "test");
        User saveUser = userRepository.save(newUser);

        //expected
        assertThrows(NotFoundProfileException.class, () -> userService.getProfile(saveUser.getId()));
    }

    @Test
    @DisplayName("파일 업데이트 - 새로운 파일 등록")
    void setNewFile() throws IOException {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpg", new FileInputStream("/Users/kh/Desktop/file/테이블.jpg"));

        User newUser = new User("bridge", Platform.APPLE, "test");

        List<Stack> skills = new ArrayList<>();
        skills.add(Stack.SPRING);
        skills.add(Stack.REDIS);

        Profile profile = new Profile("testLink", "selfIntro", "career", skills);

        newUser.getFields().add(Field.BACKEND);
        newUser.updateProfile(profile);

        User saveUser = userRepository.save(newUser);

        //when
        userService.updatePhotoFile(profile, file);

        //then
        assertEquals(1, fileRepository.count());
        assertEquals("test.jpg", fileRepository.findAll().get(0).getOriginName());
    }

    @Test
    @DisplayName("파일 업데이트 - 파일 업데이트 등록")
    void updateFile() throws IOException {
        //given
        MockMultipartFile oldFile = new MockMultipartFile("file", "old.jpg", "image/jpg", new FileInputStream("/Users/kh/Desktop/file/테이블.jpg"));
        MockMultipartFile newFile = new MockMultipartFile("file", "update.jpg", "image/jpg", new FileInputStream("/Users/kh/Desktop/file/테이블.jpg"));

        User newUser = new User("bridge", Platform.APPLE, "test");

        List<Stack> skills = new ArrayList<>();
        skills.add(Stack.SPRING);
        skills.add(Stack.REDIS);

        Profile profile = new Profile("testLink", "selfIntro", "career", skills);
        newUser.updateProfile(profile);
        User saveUser = userRepository.save(newUser);

        File file = fileService.uploadFile(oldFile);
        profile.setProfilePhoto(file);
        //when
        userService.updatePhotoFile(profile, newFile);

        //then
        assertEquals(1, fileRepository.count());
        assertEquals("update.jpg", fileRepository.findAll().get(0).getOriginName());
    }

    @Test
    @Transactional
    @DisplayName("프로필 수정 - 파일 없는 경우")
    void updateProfile() {
        //given
        User newUser = new User("bridge", Platform.APPLE, "test");

        List<Stack> skills = new ArrayList<>();
        skills.add(Stack.SPRING);
        skills.add(Stack.REDIS);

        Profile profile = new Profile("testLink", "selfIntro", "career", skills);

        newUser.getFields().add(Field.BACKEND);
        newUser.updateProfile(profile);

        User saveUser = userRepository.save(newUser);

        List<String> newSkills = new ArrayList<>();
        newSkills.add("MYSQL");

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                .selfIntro("updateIntro")
                .career("updateCareer")
                .stack(newSkills)
                .refLink("updateLink")
                .build();

        //when
        userService.updateProfile(saveUser.getId(), request, null, null);

        //then
        Profile findProfile = saveUser.getProfile();
        assertEquals("updateIntro", findProfile.getSelfIntro());
        assertEquals("updateCareer", findProfile.getCareer());
        assertEquals("MySQL", findProfile.getSkill().get(0).getValue());
    }

    @Test
    @Transactional
    @DisplayName("북마크 프로젝트 목록 조회 - 모집인원 로직 검증")
    void getBookmarkProjects_RecruitNum() {
        //given
        User newUser = new User("bridge", Platform.APPLE, "test");

        List<Part> recruits = new ArrayList<>();
        recruits.add(new Part(null, 3, null, null, null));
        recruits.add(new Part(null, 2, null, null, null));

        Project project = Project.builder()
                .title("title1")
                .overview("overview1")
                .dueDate(LocalDateTime.now().plusDays(1L))
                .uploadTime(LocalDateTime.now())
                .build();

        recruits.stream().forEach(p -> p.setProject(project));
        Project saveProject = projectRepository.save(project);

        Bookmark bookmark = new Bookmark(newUser, saveProject);

        newUser.getBookmarks().add(bookmark);
        saveProject.getBookmarks().add(bookmark);
        User saveUser = userRepository.save(newUser);

        //when
        List<BookmarkListResponse> bookmarkProjects = userService.getBookmarkProjects(saveUser.getId());

        //then
        assertEquals(5, bookmarkProjects.get(0).getRecruitTotalNum());
    }

    @Test
    @DisplayName("북마크 프로젝트 목록 조회 - 개수 검증")
    void getBookmarkProjects_Num() {
        //given
        User newUser = new User("bridge", Platform.APPLE, "test");

        List<Project> projects = IntStream.range(1, 11)
                .mapToObj(i -> Project.builder()
                        .title("title" + i)
                        .dueDate(LocalDateTime.now().plusDays(1))
                        .uploadTime(LocalDateTime.now())
                        .overview("overview" + i).build())
                .collect(Collectors.toList());

        projectRepository.saveAll(projects);

        List<Bookmark> bookmarks = projects.stream()
                .map(p -> new Bookmark(newUser, p))
                .collect(Collectors.toList());

        newUser.getBookmarks().addAll(bookmarks);
        User saveUser = userRepository.save(newUser);

        //when
        List<BookmarkListResponse> bookmarkProjects = userService.getBookmarkProjects(saveUser.getId());

        //then
        assertEquals(10, bookmarkProjects.size());
    }

    @Test
    @DisplayName("북마크 프로젝트 목록 조회 - 내용 검증")
    void getBookmarkProjects_Detail() {
        //given
        User newUser = new User("bridge", Platform.APPLE, "test");
        LocalDateTime now = LocalDateTime.now();

        Project project = Project.builder()
                .title("title")
                .overview("overview")
                .uploadTime(now)
                .dueDate(now.plusDays(24L))
                .startDate(LocalDateTime.of(2023, 12, 24, 0, 0))
                .endDate(LocalDateTime.of(2023, 12, 25, 0, 0))
                .build();

        projectRepository.save(project);

        Bookmark bookmark = new Bookmark(newUser, project);

        newUser.getBookmarks().add(bookmark);
        User saveUser = userRepository.save(newUser);

        //when
        List<BookmarkListResponse> bookmarkProjects = userService.getBookmarkProjects(saveUser.getId());

        //then
        BookmarkListResponse bookmarkResponse = bookmarkProjects.get(0);
        assertEquals("title", bookmarkResponse.getTitle());
        assertEquals(24L, bookmarkResponse.getDDay());
        assertEquals(LocalDateTime.of(2023,12,24,0,0), bookmarkResponse.getStartDate());
        assertEquals(LocalDateTime.of(2023,12,25,0,0), bookmarkResponse.getEndDate());
    }

    @Test
    @DisplayName("회원 탈퇴 기능")
    void deleteUser() {
        //given
        User newUser = new User("bridge", Platform.APPLE, "test");
        User saveUser = userRepository.save(newUser);

        //when
        boolean result = userService.deleteUser(saveUser.getId());

        //then
        assertEquals(0, userRepository.count());
        assertTrue(result);
    }
    @Test
    @DisplayName("회원 탈퇴 시 프로젝트 DB도 지워지는 지 검증")
    void deleteUserWithProjectDB() {
        //given
        User newUser = new User("bridge", Platform.APPLE, "test");

        Project project = Project.builder()
                .title("title")
                .overview("overview")
                .dueDate(LocalDateTime.now())
                .user(newUser)
                .build();

        newUser.getProjects().add(project);
        User saveUser = userRepository.save(newUser);

        //when
        userService.deleteUser(saveUser.getId());

        //then
        assertEquals(0, projectRepository.count());
    }

    @Test
    @Transactional
    @DisplayName("로그아웃 기능")
    void logout() {
        //given
        User newUser = new User("bridge", Platform.APPLE, "test");
        newUser.updateRefreshToken("refreshToken");
        User saveUser = userRepository.save(newUser);

        //when
        boolean result = userService.logout(saveUser.getId());

        //then
        assertTrue(result);
        assertNull(saveUser.getRefreshToken());
    }

    @Test
    @DisplayName("마이페이지 조회 - 관심분야만 등록 시")
    void myPage() {
        //given
        User newUser = new User("bridge", Platform.APPLE, "test");

        newUser.getFields().add(Field.BACKEND);
        newUser.getFields().add(Field.FRONTEND);

        User saveUser = userRepository.save(newUser);

        //when
        MyPageResponse myPage = userService.getMyPage(saveUser.getId());

        //then
        assertEquals("bridge", myPage.getName());
        assertEquals("백엔드", myPage.getField().get(0));
        assertEquals("프론트엔드", myPage.getField().get(1));
        assertEquals(0, myPage.getBookmarkNum());
    }

    @Test
    @DisplayName("마이페이지 조회 - 관심분야 + 북마크 시")
    void myPageHasBookmark() {
        //given
        User newUser = new User("bridge", Platform.APPLE, "test");

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

        //when
        MyPageResponse myPage = userService.getMyPage(saveUser.getId());

        //then
        assertEquals("bridge", myPage.getName());
        assertEquals("백엔드", myPage.getField().get(0));
        assertEquals("프론트엔드", myPage.getField().get(1));
        assertEquals(1, myPage.getBookmarkNum());
    }
}