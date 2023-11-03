package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.ApplyProject;
import com.Bridge.bridge.domain.Bookmark;
import com.Bridge.bridge.domain.Field;
import com.Bridge.bridge.domain.Part;
import com.Bridge.bridge.domain.Platform;
import com.Bridge.bridge.domain.Profile;
import com.Bridge.bridge.domain.Project;
import com.Bridge.bridge.domain.SearchWord;
import com.Bridge.bridge.domain.Stack;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.FilterRequestDto;
import com.Bridge.bridge.dto.request.ProjectUpdateRequestDto;
import com.Bridge.bridge.dto.response.*;
import com.Bridge.bridge.dto.request.PartRequestDto;
import com.Bridge.bridge.dto.request.ProjectRequestDto;
import com.Bridge.bridge.repository.BookmarkRepository;
import com.Bridge.bridge.exception.notfound.NotFoundProjectException;
import com.Bridge.bridge.repository.ApplyProjectRepository;
import com.Bridge.bridge.repository.ProjectRepository;
import com.Bridge.bridge.repository.SearchWordRepository;
import com.Bridge.bridge.repository.UserRepository;
import com.Bridge.bridge.security.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectServiceTest {

    @Autowired
    ProjectService projectService;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BookmarkRepository bookmarkRepository;
    @Autowired
    SearchWordRepository searchWordRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ApplyProjectRepository applyProjectRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @DisplayName("모집글 검색 기능 test")
    @Test
    public void findProjects() {
        // given
        User user = new User("create", "create@gmail.com", Platform.APPLE, "updateTest");
        userRepository.save(user);

        List<Stack> skill1 = new ArrayList<>();
        skill1.add(Stack.JAVA);
        skill1.add(Stack.SPRINGBOOT);

        List<Stack> skill2 = new ArrayList<>();
        skill1.add(Stack.JAVA);
        skill1.add(Stack.SPRINGBOOT);

        List<Part> recruit1 = new ArrayList<>();
        recruit1.add(Part.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill1)
                .requirement("아무거나")
                .build());

        List<Part> recruit2 = new ArrayList<>();
        recruit2.add(Part.builder()
                .recruitPart("frontend")
                .recruitNum(2)
                .recruitSkill(skill2)
                .requirement("아무거나")
                .build());

        LocalDateTime dueDate = LocalDateTime.of(2024,1,12,0,0,0);
        LocalDateTime startDate = LocalDateTime.of(2023,2,12,0,0,0);
        LocalDateTime endDate = LocalDateTime.of(2023,3,12,0,0,0);

        Project newProject1 = Project.builder()
                .title("어플 프로젝트")
                .overview("This is new Project.")
                .dueDate(dueDate)
                .startDate(startDate)
                .endDate(endDate)
                .recruit(recruit1)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user)
                .stage("Before Start")
                .build();

        Project newProject2 = Project.builder()
                .title("New project")
                .overview("This is 맛집 어프")
                .dueDate(dueDate)
                .startDate(startDate)
                .endDate(endDate)
                .recruit(recruit2)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user)
                .stage("Before Start")
                .build();

        projectRepository.save(newProject1);
        projectRepository.save(newProject2);


        // When
        List<ProjectListResponseDto> result = projectService.findByTitleAndContent(user.getId(),"어플");

        // Then
        assertEquals(result.get(0).getTitle(),"어플 프로젝트" );

    }

    @DisplayName("모집글 생성 기능 test")
    @Test
    void createProject() {
        // given

        User user = new User("create", "create@gmail.com", Platform.APPLE, "updateTest");
        userRepository.save(user);

        List<String> skill = new ArrayList<>();
        skill.add("JAVA");
        skill.add("SPRINGBOOT");

        List<PartRequestDto> recruit = new ArrayList<>();
        recruit.add(PartRequestDto.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill)
                .requirement("아무거나")
                .build());

        LocalDateTime dueDate = LocalDateTime.of(2024,1,12,0,0,0);
        LocalDateTime startDate = LocalDateTime.of(2023,2,12,0,0,0);
        LocalDateTime endDate = LocalDateTime.of(2023,3,12,0,0,0);

        ProjectRequestDto newProject = ProjectRequestDto.builder()
                .title("New project")
                .overview("This is new Project.")
                .dueDate(dueDate.toString())
                .startDate(startDate.toString())
                .endDate(endDate.toString())
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .userId(user.getId())
                .stage("Before Start")
                .build();

        // when
        Long newProjectId = projectService.createProject(newProject);

        // then
        assertThat(newProjectId).isNotEqualTo(null);
    }

    @DisplayName("프로젝트 삭제 기능 - 삭제하려는 유저가 DB에 있을 때(올바른 접근)")
    @Test
    void deleteProject() {
        // given

        User user = new User("delete", "delete@gmail.com", Platform.APPLE, "updateTest");
        userRepository.save(user);

        List<Stack> skill1 = new ArrayList<>();
        skill1.add(Stack.JAVA);
        skill1.add(Stack.SPRINGBOOT);

        List<Part> recruit1 = new ArrayList<>();
        recruit1.add(Part.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill1)
                .requirement("아무거나")
                .build());

        LocalDateTime dueDate = LocalDateTime.of(2024,1,12,0,0,0);
        LocalDateTime startDate = LocalDateTime.of(2023,2,12,0,0,0);
        LocalDateTime endDate = LocalDateTime.of(2023,3,12,0,0,0);

        Project newProject1 = Project.builder()
                .title("어플 프로젝트")
                .overview("This is new Project.")
                .dueDate(dueDate)
                .startDate(startDate)
                .endDate(endDate)
                .recruit(recruit1)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user)
                .stage("Before Start")
                .build();

        Project saveProject = projectRepository.save(newProject1);

        // when
        Boolean result = projectService.deleteProject(saveProject.getId());

        // then
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("프로젝트 모집글 수정 테스트")
    void updateProject() {
        // given
        User user = new User("update", "update@gmail.com", Platform.APPLE, "updateTest");
        userRepository.save(user);

        List<Stack> skill = new ArrayList<>();
        skill.add(Stack.JAVA);
        skill.add(Stack.SPRINGBOOT);

        List<Part> recruit = new ArrayList<>();
        recruit.add(Part.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill)
                .requirement("아무거나")
                .build());

        LocalDateTime dueDate = LocalDateTime.of(2024,1,12,0,0,0);
        LocalDateTime startDate = LocalDateTime.of(2023,2,12,0,0,0);
        LocalDateTime endDate = LocalDateTime.of(2023,3,12,0,0,0);


        Project newProject = Project.builder()
                .title("어플 프로젝트")
                .overview("This is new Project.")
                .dueDate(dueDate)
                .startDate(startDate)
                .endDate(endDate)
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user)
                .stage("Before Start")
                .build();


        recruit.get(0).setProject(newProject);
        projectRepository.save(newProject);


        List<String> updateSkill = new ArrayList<>();
        updateSkill.add("JAVASCRIPT");
        updateSkill.add("REACT");

        List<PartRequestDto> updateRecruit = new ArrayList<>();
        updateRecruit.add(PartRequestDto.builder()
                .recruitPart("frontend")
                .recruitNum(2)
                .recruitSkill(updateSkill)
                .requirement("화이팅")
                .build());

        ProjectUpdateRequestDto updateProject = ProjectUpdateRequestDto.builder()
                .title("Update project")
                .overview("This is Updated Project.")
                .dueDate(dueDate)
                .startDate(startDate)
                .endDate(endDate)
                .recruit(updateRecruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .stage("Before Start")
                .build();


        // when
        ProjectResponseDto result = projectService.updateProject(newProject.getId(), updateProject);

        // then
        assertThat(result.getTitle()).isEqualTo("Update project");
    }

    @Test
    @DisplayName("프로젝트 모집글 수정 테스트 _ 잘못된 프로젝트ID")
    void updateProject_wrongProjectId() {
        // given
        User user1 = new User("update", "update2@gmail.com", Platform.APPLE, "update1Test");
        userRepository.save(user1);

        List<Stack> skill = new ArrayList<>();
        skill.add(Stack.JAVA);
        skill.add(Stack.SPRINGBOOT);

        List<Part> recruit = new ArrayList<>();
        recruit.add(Part.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill)
                .requirement("아무거나")
                .build());

        LocalDateTime dueDate = LocalDateTime.of(2024,1,12,0,0,0);
        LocalDateTime startDate = LocalDateTime.of(2023,2,12,0,0,0);
        LocalDateTime endDate = LocalDateTime.of(2023,3,12,0,0,0);


        Project newProject = Project.builder()
                .title("New project")
                .overview("This is new Project.")
                .dueDate(dueDate)
                .startDate(startDate)
                .endDate(endDate)
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user1)
                .stage("Before Start")
                .build();
        projectRepository.save(newProject);


        List<String> updateSkill = new ArrayList<>();
        updateSkill.add("Javascript");
        updateSkill.add("React");

        List<PartRequestDto> updateRecruit = new ArrayList<>();
        updateRecruit.add(PartRequestDto.builder()
                .recruitPart("frontend")
                .recruitNum(2)
                .recruitSkill(updateSkill)
                .requirement("화이팅")
                .build());

        ProjectUpdateRequestDto updateProject = ProjectUpdateRequestDto.builder()
                .title("Update project")
                .overview("This is Updated Project.")
                .dueDate(dueDate)
                .startDate(startDate)
                .endDate(endDate)
                .recruit(updateRecruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .stage("Before Start")
                .build();

        Long wrongId = Long.valueOf(123456789);

        // expected
        assertThrows(NotFoundProjectException.class, () -> projectService.updateProject(wrongId, updateProject));
    }

    @DisplayName("모집글 상세보기 기능")
    @Test
    void detailProject() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        User user1 = new User("find", "find@gmail.com", Platform.APPLE, "find1Test");
        User saveUser = userRepository.save(user1);

        List<Stack> skill = new ArrayList<>();
        skill.add(Stack.JAVA);
        skill.add(Stack.SPRINGBOOT);

        List<Part> recruit = new ArrayList<>();
        recruit.add(Part.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill)
                .requirement("아무거나")
                .build());

        LocalDateTime dueDate = LocalDateTime.of(2024,1,12,0,0,0);
        LocalDateTime startDate = LocalDateTime.of(2023,2,12,0,0,0);
        LocalDateTime endDate = LocalDateTime.of(2023,3,12,0,0,0);


        Project newProject = Project.builder()
                .title("New project")
                .overview("This is new Project.")
                .dueDate(dueDate)
                .startDate(startDate)
                .endDate(endDate)
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user1)
                .stage("Before Start")
                .build();

        Project theProject = projectRepository.save(newProject);

        String token = Jwts.builder()
                .setSubject(String.valueOf(saveUser.getId()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        request.addHeader("Authorization", "Bearer " + token);

        // when
        ProjectResponseDto result = projectService.getProject(theProject.getId(), request);

        // then
        assertThat(result.getTitle()).isEqualTo(newProject.getTitle());
        assertThat(result.isMyProject()).isEqualTo(true);
    }

    @DisplayName("모집글 상세보기 기능 - 잘못된 모집글 Id")
    @Test
    void detailProject_wrongProjectId() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        User user1 = new User("detail_wrong", "detail_wrong@gmail.com", Platform.APPLE, "detail_wrongTest");
        User saveUser = userRepository.save(user1);

        List<Stack> skill = new ArrayList<>();
        skill.add(Stack.JAVA);
        skill.add(Stack.SPRINGBOOT);

        List<Part> recruit = new ArrayList<>();
        recruit.add(Part.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill)
                .requirement("아무거나")
                .build());

        LocalDateTime dueDate = LocalDateTime.of(2024,1,12,0,0,0);
        LocalDateTime startDate = LocalDateTime.of(2023,2,12,0,0,0);
        LocalDateTime endDate = LocalDateTime.of(2023,3,12,0,0,0);


        Project newProject = Project.builder()
                .title("New project")
                .overview("This is new Project.")
                .dueDate(dueDate)
                .startDate(startDate)
                .endDate(endDate)
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user1)
                .stage("Before Start")
                .build();

        Project theProject = projectRepository.save(newProject);

        String token = Jwts.builder()
                .setSubject(String.valueOf(saveUser.getId()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        request.addHeader("Authorization", "Bearer " + token);

        // expected
        assertThrows(NotFoundProjectException.class,
                () -> {
                    ProjectResponseDto result = projectService.getProject(theProject.getId() + Long.valueOf(123), request);
                });
    }

    @DisplayName("모집글 상세보기 기능 - 내가 만든 프로젝트가 아닌 경우")
    @Test
    void detailProjectNotMine() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        User user1 = new User("find", "find@gmail.com", Platform.APPLE, "find1Test");
        User saveUser = userRepository.save(user1);

        List<Stack> skill = new ArrayList<>();
        skill.add(Stack.JAVA);
        skill.add(Stack.SPRINGBOOT);

        List<Part> recruit = new ArrayList<>();
        recruit.add(Part.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill)
                .requirement("아무거나")
                .build());

        LocalDateTime dueDate = LocalDateTime.of(2024,1,12,0,0,0);
        LocalDateTime startDate = LocalDateTime.of(2023,2,12,0,0,0);
        LocalDateTime endDate = LocalDateTime.of(2023,3,12,0,0,0);


        Project newProject = Project.builder()
                .title("New project")
                .overview("This is new Project.")
                .dueDate(dueDate)
                .startDate(startDate)
                .endDate(endDate)
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user1)
                .stage("Before Start")
                .build();

        Project theProject = projectRepository.save(newProject);

        String token = Jwts.builder()
                .setSubject(String.valueOf(saveUser.getId()+1L))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        request.addHeader("Authorization", "Bearer " + token);

        // when
        ProjectResponseDto result = projectService.getProject(theProject.getId(), request);

        // then
        assertThat(result.isMyProject()).isEqualTo(false);
    }
    
    @DisplayName("모집글 필터링")
    @Test
    @Transactional
    void filtering() {
        // given
        User user = new User("user", "user2@gmail.com", Platform.APPLE, "Test");
        userRepository.save(user);

        List<Stack> skill1 = new ArrayList<>();
        skill1.add(Stack.JAVA);
        skill1.add(Stack.SPRINGBOOT);

        List<Stack> skill2 = new ArrayList<>();
        skill2.add(Stack.JAVA);
        skill2.add(Stack.JAVASCRIPT);
        skill2.add(Stack.SPRINGBOOT);
        skill2.add(Stack.NODEJS);

        List<Part> recruit = new ArrayList<>();
        recruit.add(Part.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill1)
                .requirement("아무거나")
                .build());
        recruit.add(Part.builder()
                .recruitPart("frontend")
                .recruitNum(1)
                .recruitSkill(skill2)
                .requirement("skill2")
                .build());

        List<Part> recruit2 = new ArrayList<>();
        recruit2.add(Part.builder()
                .recruitPart("frontend")
                .recruitNum(1)
                .recruitSkill(skill2)
                .requirement("skill2")
                .build());

        LocalDateTime dueDate = LocalDateTime.of(2024,1,12,0,0,0);
        LocalDateTime startDate = LocalDateTime.of(2023,2,12,0,0,0);
        LocalDateTime endDate = LocalDateTime.of(2023,3,12,0,0,0);


        Project newProject1 = Project.builder()
                .title("New project")
                .overview("This is new Project.")
                .dueDate(dueDate)
                .startDate(startDate)
                .endDate(endDate)
                .recruit(new ArrayList<>())
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user)
                .stage("Before Start")
                .build();

        recruit.stream()
                .forEach((part -> part.setProject(newProject1)));

        Project newProject2 = Project.builder()
                .title("New project")
                .overview("This is new Project.")
                .dueDate(dueDate)
                .startDate(startDate)
                .endDate(endDate)
                .recruit(new ArrayList<>())
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user)
                .stage("Before Start")
                .build();

        recruit2.stream()
                .forEach((part -> part.setProject(newProject2)));

        projectRepository.save(newProject1);
        projectRepository.save(newProject2);

        List<String> findSkills = new ArrayList<>();
        findSkills.add("JAVA");
        findSkills.add("SPRINGBOOT");

        FilterRequestDto filterRequestDto = FilterRequestDto.builder()
                .part("backend")
                .skills(findSkills)
                .build();

        // when
        int result = projectService.filterProjectList(filterRequestDto).size();

        // then
        assertThat(result).isEqualTo(1);
        assertThat(newProject1.getRecruit()).isNotEqualTo(null);
    }

    @Test
    @DisplayName("지원한 프로젝트 목록 반환 - 개수 확인")
    void getApplyProjectsNum() {
        //given
        User user1 = new User("bridge1", "bridge1@apple.com", Platform.APPLE, "1");
        User user2 = new User("bridge2", "bridge2@apple.com", Platform.APPLE, "1");
        userRepository.save(user1);

        Project project1 = Project.builder()
                .title("title1")
                .overview("overview1")
                .user(user1)
                .stage("stage1")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0))
                .build();

        Project project2 = Project.builder()
                .title("title2")
                .overview("overview2")
                .user(user1)
                .stage("stage2")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0))
                .build();

        projectRepository.save(project1);
        projectRepository.save(project2);

        ApplyProject applyProject1 = new ApplyProject();
        applyProject1.setUserAndProject(user2, project1);
        ApplyProject applyProject2 = new ApplyProject();
        applyProject2.setUserAndProject(user2, project2);

        user2.getApplyProjects().add(applyProject1);
        user2.getApplyProjects().add(applyProject2);
        User saveUser2 = userRepository.save(user2);

        //when
        List<ApplyProjectResponse> applyProjects = projectService.getApplyProjects(saveUser2.getId());

        //then
        assertEquals(2, applyProjectRepository.count());
        assertEquals(2, saveUser2.getApplyProjects().size());
        assertEquals(2, applyProjects.size());
    }

    @Test
    @DisplayName("지원한 프로젝트 목록 반환 - 내용 확인")
    void getApplyProjectsDetail() {
        //given
        User user1 = new User("bridge1", "bridge1@apple.com", Platform.APPLE, "1");

        Project project1 = Project.builder()
                .title("title1")
                .overview("overview1")
                .stage("stage1")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0))
                .build();

        projectRepository.save(project1);

        ApplyProject applyProject1 = new ApplyProject();
        applyProject1.setUserAndProject(user1, project1);


        user1.getApplyProjects().add(applyProject1);
        User saveUser1 = userRepository.save(user1);

        //when
        List<ApplyProjectResponse> applyProjects = projectService.getApplyProjects(saveUser1.getId());

        //then
        ApplyProjectResponse response = applyProjects.get(0);
        assertEquals("결과 대기중", response.getStage());
        assertEquals("title1", response.getTitle());
        assertEquals("overview1", response.getOverview());
        assertEquals(String.valueOf(LocalDateTime.of(2024,1,12,0,0,0)), response.getDueDate());
    }
    @Test
    @Transactional
    @DisplayName("프로젝트 지원하기")
    void apply() {
        //given
        User user1 = new User("bridge1", "bridge1@apple.com", Platform.APPLE, "1");
        User user2 = new User("bridge2", "bridge2@apple.com", Platform.APPLE, "1");
        userRepository.save(user1);

        Project project1 = Project.builder()
                .title("title1")
                .overview("overview1")
                .user(user1)
                .stage("stage1")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0))
                .build();

        Project saveProject = projectRepository.save(project1);
        User saveUser2 = userRepository.save(user2);

        //when
        boolean apply = projectService.apply(saveUser2.getId(), saveProject.getId());

        //then
        assertEquals(1, applyProjectRepository.count());
        assertEquals(1, saveUser2.getApplyProjects().size());
        assertEquals(1, saveProject.getApplyProjects().size());
        assertTrue(apply);
    }

    @DisplayName("내가 작성한 모집글")
    @Test
    void findMyProject() {
        // given
        User user = new User("user", "user2@gmail.com", Platform.APPLE, "Test");
        userRepository.save(user);

        List<Stack> skill1 = new ArrayList<>();
        skill1.add(Stack.JAVA);
        skill1.add(Stack.SPRINGBOOT);

        List<Stack> skill2 = new ArrayList<>();
        skill2.add(Stack.JAVA);
        skill2.add(Stack.JAVASCRIPT);
        skill2.add(Stack.SPRINGBOOT);
        skill2.add(Stack.NODEJS);

        List<Part> recruit = new ArrayList<>();
        recruit.add(Part.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill1)
                .requirement("아무거나")
                .build());

        List<Part> recruit2 = new ArrayList<>();
        recruit2.add(Part.builder()
                .recruitPart("frontend")
                .recruitNum(1)
                .recruitSkill(skill2)
                .requirement("skill2")
                .build());

        Project newProject1 = Project.builder()
                .title("Find MyProject1")
                .overview("This is My Project1")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0))
                .startDate(LocalDateTime.of(2024,2,12,0,0,0))
                .endDate(LocalDateTime.of(2024,3,12,0,0,0))
                .recruit(new ArrayList<>())
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user)
                .stage("Before Start")
                .build();

        recruit.stream()
                .forEach((part -> part.setProject(newProject1)));

        Project newProject2 = Project.builder()
                .title("Find MyProject2")
                .overview("This is My Project2")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0))
                .startDate(LocalDateTime.of(2024,1,12,0,0,0))
                .endDate(LocalDateTime.of(2024,1,12,0,0,0))
                .recruit(new ArrayList<>())
                .tagLimit(new ArrayList<>())
                .meetingWay("ONline")
                .user(user)
                .stage("Before Start")
                .build();

        recruit2.stream()
                .forEach((part -> part.setProject(newProject2)));

        projectRepository.save(newProject1);
        projectRepository.save(newProject2);

        // when
        List<MyProjectResponseDto> response = projectService.findMyProjects(user.getId());

        // then
        Assertions.assertThat(response.size()).isEqualTo(2);
    }

    @DisplayName("내가 작성한 모집글 불러오기 - 하나도 없을 때")
    @Test
    void NoProjects() {
        // given
        User user = new User("user", "user2@gmail.com", Platform.APPLE, "Test");
        userRepository.save(user);

        // when

        assertThrows(NotFoundProjectException.class, () -> projectService.findMyProjects(user.getId()));

    }

    @DisplayName("모든 모집글")
    @Test
    void findAllProject() {
        // given
        User user1 = new User("user1", "user1@gmail.com", Platform.APPLE, "Test");
        userRepository.save(user1);

        User user2 = new User("user2", "user2@gmail.com", Platform.APPLE, "Test");
        userRepository.save(user2);

        User user3 = new User("user3", "user3@gmail.com", Platform.APPLE, "Test");
        userRepository.save(user3);

        List<Stack> skill1 = new ArrayList<>();
        skill1.add(Stack.JAVA);
        skill1.add(Stack.SPRINGBOOT);

        List<Stack> skill2 = new ArrayList<>();
        skill2.add(Stack.JAVA);
        skill2.add(Stack.JAVASCRIPT);
        skill2.add(Stack.SPRINGBOOT);
        skill2.add(Stack.NODEJS);

        List<Stack> skill3 = new ArrayList<>();
        skill2.add(Stack.PYTHON);
        skill2.add(Stack.JAVA);
        skill2.add(Stack.SPRINGBOOT);
        skill2.add(Stack.DJANGO);

        List<Part> recruit = new ArrayList<>();
        recruit.add(Part.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill1)
                .requirement("아무거나")
                .build());

        List<Part> recruit2 = new ArrayList<>();
        recruit2.add(Part.builder()
                .recruitPart("frontend")
                .recruitNum(1)
                .recruitSkill(skill2)
                .requirement("skill2")
                .build());

        List<Part> recruit3 = new ArrayList<>();
        recruit3.add(Part.builder()
                .recruitPart("backend")
                .recruitNum(5)
                .recruitSkill(skill3)
                .requirement("skill3")
                .build());

        Project newProject1 = Project.builder()
                .title("Find AllProject1")
                .overview("This is My Project1")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0))
                .startDate(LocalDateTime.of(2024,1,12,0,0,0))
                .endDate(LocalDateTime.of(2024,1,12,0,0,0))
                .recruit(new ArrayList<>())
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user1)
                .stage("Before Start")
                .build();

        recruit.stream()
                .forEach((part -> part.setProject(newProject1)));

        Project newProject2 = Project.builder()
                .title("Find AllProject2")
                .overview("This is My Project2")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0))
                .startDate(LocalDateTime.of(2024,1,12,0,0,0))
                .endDate(LocalDateTime.of(2024,1,12,0,0,0))
                .recruit(new ArrayList<>())
                .tagLimit(new ArrayList<>())
                .meetingWay("ONline")
                .user(user2)
                .stage("Before Start")
                .build();

        recruit2.stream()
                .forEach((part -> part.setProject(newProject2)));

        Project newProject3 = Project.builder()
                .title("Find AllProject3")
                .overview("This is My Project3")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0))
                .startDate(LocalDateTime.of(2024,1,12,0,0,0))
                .endDate(LocalDateTime.of(2024,1,12,0,0,0))
                .recruit(new ArrayList<>())
                .tagLimit(new ArrayList<>())
                .meetingWay("ONline")
                .user(user3)
                .stage("Before Start")
                .build();

        recruit3.stream()
                .forEach((part -> part.setProject(newProject3)));

        projectRepository.save(newProject1);
        projectRepository.save(newProject2);
        projectRepository.save(newProject3);

        // when
        List<ProjectListResponseDto> response = projectService.allProjects();

        // then
        Assertions.assertThat(response.size()).isEqualTo(3);
    }

    @DisplayName("내 분야 모집글")
    @Test
    void findMyPartProjects() {
        // given
        User user1 = new User("user", "user2@gmail.com", Platform.APPLE, "Test");
        userRepository.save(user1);

        List<Stack> skill1 = new ArrayList<>();
        skill1.add(Stack.JAVA);
        skill1.add(Stack.SPRINGBOOT);

        List<Stack> skill2 = new ArrayList<>();
        skill2.add(Stack.JAVA);
        skill2.add(Stack.JAVASCRIPT);
        skill2.add(Stack.SPRINGBOOT);
        skill2.add(Stack.NODEJS);

        List<Stack> skill3 = new ArrayList<>();
        skill2.add(Stack.PYTHON);
        skill2.add(Stack.JAVA);
        skill2.add(Stack.SPRINGBOOT);
        skill2.add(Stack.DJANGO);

        List<Part> recruit = new ArrayList<>();
        recruit.add(Part.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill1)
                .requirement("아무거나")
                .build());

        List<Part> recruit2 = new ArrayList<>();
        recruit2.add(Part.builder()
                .recruitPart("frontend")
                .recruitNum(1)
                .recruitSkill(skill2)
                .requirement("skill2")
                .build());

        List<Part> recruit3 = new ArrayList<>();
        recruit3.add(Part.builder()
                .recruitPart("backend")
                .recruitNum(5)
                .recruitSkill(skill3)
                .requirement("skill3")
                .build());

        Project newProject1 = Project.builder()
                .title("Find AllProject1")
                .overview("This is My Project1")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0))
                .startDate(LocalDateTime.of(2024,1,12,0,0,0))
                .endDate(LocalDateTime.of(2024,1,12,0,0,0))
                .recruit(new ArrayList<>())
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user1)
                .stage("Before Start")
                .build();

        recruit.stream()
                .forEach((part -> part.setProject(newProject1)));

        Project newProject2 = Project.builder()
                .title("Find AllProject2")
                .overview("This is My Project2")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0))
                .startDate(LocalDateTime.of(2024,1,12,0,0,0))
                .endDate(LocalDateTime.of(2024,1,12,0,0,0))
                .recruit(new ArrayList<>())
                .tagLimit(new ArrayList<>())
                .meetingWay("ONline")
                .user(user1)
                .stage("Before Start")
                .build();

        recruit2.stream()
                .forEach((part -> part.setProject(newProject2)));

        Project newProject3 = Project.builder()
                .title("Find AllProject3")
                .overview("This is My Project3")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0))
                .startDate(LocalDateTime.of(2024,1,12,0,0,0))
                .endDate(LocalDateTime.of(2024,1,12,0,0,0))
                .recruit(new ArrayList<>())
                .tagLimit(new ArrayList<>())
                .meetingWay("ONline")
                .user(user1)
                .stage("Before Start")
                .build();

        recruit3.stream()
                .forEach((part -> part.setProject(newProject3)));

        projectRepository.save(newProject1);
        projectRepository.save(newProject2);
        projectRepository.save(newProject3);

        // when
        List<ProjectListResponseDto> response = projectService.findMyPartProjects("backend");

        // then
        Assertions.assertThat(response.size()).isEqualTo(2);
    }

    @DisplayName("모집글 마감 기능")
    @Test
    void deadline() {
        // given
        User user = new User("updateDeadline", "updateDeadline@gmail.com", Platform.APPLE, "updateDeadlineTest");
        userRepository.save(user);

        List<Stack> skill = new ArrayList<>();
        skill.add(Stack.JAVA);
        skill.add(Stack.SPRINGBOOT);

        List<Part> recruit = new ArrayList<>();
        recruit.add(Part.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill)
                .requirement("아무거나")
                .build());


        Project newProject = Project.builder()
                .title("New project")
                .overview("This is new Project.")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0))
                .startDate(LocalDateTime.of(2024,1,12,0,0,0))
                .endDate(LocalDateTime.of(2024,1,12,0,0,0))
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user)
                .stage("Before Start")
                .build();

        recruit.get(0).setProject(newProject);
        projectRepository.save(newProject);

        // when
        ProjectResponseDto response = projectService.closeProject(newProject.getId());

        LocalDateTime localDateTime = LocalDateTime.now();
        String formatedNow = localDateTime.format(DateTimeFormatter.ofPattern("YYYYMMDDHHmmss"));

        // then
        Assertions.assertThat(response.getDueDate()).isNotEqualTo(newProject.getDueDate());
    }

    @DisplayName("모집글 마감 기능_이미 마감된 모집글")
    @Test
    void alreadyClosed() {
        // given
        User user = new User("alreadyClosed", "alreadyClosed@gmail.com", Platform.APPLE, "alreadyClosedTest");
        userRepository.save(user);

        List<Stack> skill = new ArrayList<>();
        skill.add(Stack.JAVA);
        skill.add(Stack.SPRINGBOOT);

        List<Part> recruit = new ArrayList<>();
        recruit.add(Part.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill)
                .requirement("아무거나")
                .build());


        Project newProject = Project.builder()
                .title("New project")
                .overview("This is new Project.")
                .dueDate(LocalDateTime.of(2023,1,12,0,0,0))
                .startDate(LocalDateTime.of(2024,1,12,0,0,0))
                .endDate(LocalDateTime.of(2024,1,12,0,0,0))
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user)
                .stage("Before Start")
                .build();

        recruit.get(0).setProject(newProject);
        projectRepository.save(newProject);

        // when
        System.out.println(assertThrows(IllegalStateException.class, ()-> projectService.closeProject(newProject.getId()))
                .getMessage());
    }

    @DisplayName("모집글 스크랩 기능")
    @Test
    void scrap() {
        // given
        User user = new User("scrap", "scrap@gmail.com", Platform.APPLE, "scrapTest");
        user = userRepository.save(user);

        List<Stack> skill = new ArrayList<>();
        skill.add(Stack.JAVA);
        skill.add(Stack.SPRINGBOOT);

        List<Part> recruit = new ArrayList<>();
        recruit.add(Part.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill)
                .requirement("아무거나")
                .build());


        Project newProject = Project.builder()
                .title("New project")
                .overview("This is new Project.")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0))
                .startDate(LocalDateTime.of(2024,1,12,0,0,0))
                .endDate(LocalDateTime.of(2024,1,12,0,0,0))
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user)
                .stage("Before Start")
                .build();

        recruit.get(0).setProject(newProject);
        newProject = projectRepository.save(newProject);

        // when

        BookmarkResponseDto bookmarkResponseDto = projectService.scrap(newProject.getId(), user.getId());
        Assertions.assertThat(bookmarkResponseDto.getScrap()).isEqualTo("스크랩이 설정되었습니다.");

    }

    @DisplayName("모집글 스크랩 해제")
    @Test
    void unscrap() {
        // given
        User user = new User("user1", "user1@gmail.com", Platform.APPLE, "Test");
        user = userRepository.save(user);

        List<Stack> skill = new ArrayList<>();
        skill.add(Stack.JAVA);
        skill.add(Stack.SPRINGBOOT);

        List<Part> recruit = new ArrayList<>();
        recruit.add(Part.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill)
                .requirement("아무거나")
                .build());


        Project newProject = Project.builder()
                .title("New project")
                .overview("This is new Project.")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0))
                .startDate(LocalDateTime.of(2024,1,12,0,0,0))
                .endDate(LocalDateTime.of(2024,1,12,0,0,0))
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user)
                .stage("Before Start")
                .build();

        recruit.get(0).setProject(newProject);
        newProject = projectRepository.save(newProject);

        Bookmark newBookmark = Bookmark.builder()
                .user(user)
                .project(newProject)
                .build();

        newBookmark = bookmarkRepository.save(newBookmark);

        // user - bookmark 연관관계 맵핑
        user.setBookmarks(newBookmark);
        userRepository.save(user);

        // project - bookmark 연관관계 맵핑
        newProject.setBookmarks(newBookmark);
        projectRepository.save(newProject);

        // when
        BookmarkResponseDto bookmarkResponseDto = projectService.scrap(newProject.getId(), user.getId());
        Assertions.assertThat(bookmarkResponseDto.getScrap()).isEqualTo("스크랩이 해제되었습니다.");
    }

    @Test
    @DisplayName("프로젝트 지원 취소하기")
    @Transactional
    void cancelApply() {
        //given
        User user1 = new User("bridge1", "bridge1@apple.com", Platform.APPLE, "1");

        Project project1 = Project.builder()
                .title("title1")
                .overview("overview1")
                .stage("stage1")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0))
                .build();

        Project saveProject = projectRepository.save(project1);

        ApplyProject applyProject1 = new ApplyProject();
        applyProject1.setUserAndProject(user1, project1);


        user1.getApplyProjects().add(applyProject1);
        User saveUser1 = userRepository.save(user1);

        //when
        boolean cancelApply = projectService.cancelApply(saveUser1.getId(), saveProject.getId());

        //then
        assertEquals(0, applyProjectRepository.count());
        assertEquals(0, saveUser1.getApplyProjects().size());
        assertEquals(0, saveProject.getApplyProjects().size());
        assertTrue(cancelApply);
    }

    @Test
    @DisplayName("프로젝트 지원자 목록 - 지원자 수 확인")
    void getApplyUsersNum() {
        //given
        User user1 = new User("bridge1", "bridge1@apple.com", Platform.APPLE, "1");
        User user2 = new User("bridge2", "bridge2@apple.com", Platform.APPLE, "2");
        User user3 = new User("bridge3", "bridge3@apple.com", Platform.APPLE, "3");

        user1.getFields().add(Field.BACKEND);
        user2.getFields().add(Field.FRONTEND);

        Profile profile1 = Profile.builder()
                .career("career1")
                .build();

        Profile profile2 = Profile.builder()
                .career("career2")
                .build();

        user1.updateProfile(profile1);
        user2.updateProfile(profile2);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        Project project1 = Project.builder()
                .title("title1")
                .overview("overview1")
                .stage("stage1")
                .user(user3)
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0))
                .build();

        ApplyProject applyProject1 = new ApplyProject();
        applyProject1.setUserAndProject(user1, project1);
        ApplyProject applyProject2 = new ApplyProject();
        applyProject2.setUserAndProject(user2, project1);

        project1.getApplyProjects().add(applyProject1);
        project1.getApplyProjects().add(applyProject2);
        Project saveProject = projectRepository.save(project1);


        //when
        List<ApplyUserResponse> applyUsers = projectService.getApplyUsers(saveProject.getId());

        //then
        assertEquals(2, applyUsers.size());
    }

    @Test
    @DisplayName("프로젝트 지원자 목록 - 수락 or 거절한 지원자는 반영x 확인")
    void getApplyUsersNumAcceptOrReject() {
        //given
        User user1 = new User("bridge1", "bridge1@apple.com", Platform.APPLE, "1");
        User user2 = new User("bridge2", "bridge2@apple.com", Platform.APPLE, "2");
        User user3 = new User("bridge3", "bridge3@apple.com", Platform.APPLE, "3");

        user1.getFields().add(Field.BACKEND);
        user2.getFields().add(Field.FRONTEND);


        Profile profile1 = Profile.builder()
                .career("career1")
                .build();

        Profile profile2 = Profile.builder()
                .career("career2")
                .build();

        user1.updateProfile(profile1);
        user2.updateProfile(profile2);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        Project project1 = Project.builder()
                .title("title1")
                .overview("overview1")
                .stage("stage1")
                .user(user3)
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0))
                .build();

        ApplyProject applyProject1 = new ApplyProject();
        applyProject1.setUserAndProject(user1, project1);
        applyProject1.changeStage("수락");
        ApplyProject applyProject2 = new ApplyProject();
        applyProject2.setUserAndProject(user2, project1);

        project1.getApplyProjects().add(applyProject1);
        project1.getApplyProjects().add(applyProject2);
        Project saveProject = projectRepository.save(project1);


        //when
        List<ApplyUserResponse> applyUsers = projectService.getApplyUsers(saveProject.getId());

        //then
        assertEquals(1, applyUsers.size());
    }

    @Test
    @DisplayName("프로젝트 지원자 목록 - 지원자 내용 확인")
    void getApplyUsersDetail() {
        //given
        User user1 = new User("bridge1", "bridge1@apple.com", Platform.APPLE, "1");

        user1.getFields().add(Field.BACKEND);

        Profile profile1 = Profile.builder()
                .career("career1")
                .build();

        user1.updateProfile(profile1);

        userRepository.save(user1);

        Project project1 = Project.builder()
                .title("title1")
                .overview("overview1")
                .stage("stage1")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0))
                .build();

        ApplyProject applyProject1 = new ApplyProject();
        applyProject1.setUserAndProject(user1, project1);

        project1.getApplyProjects().add(applyProject1);
        Project saveProject = projectRepository.save(project1);


        //when
        List<ApplyUserResponse> applyUsers = projectService.getApplyUsers(saveProject.getId());

        //then
        ApplyUserResponse response = applyUsers.get(0);
        assertEquals("bridge1", response.getName());
        assertEquals("백엔드", response.getFields().get(0));
        assertEquals("career1", response.getCareer());
    }

    @Test
    @DisplayName("프로젝트 수락하기 - 일치하는 경우")
    void acceptApply() {
        //given
        User user1 = new User("bridge1", "bridge1@apple.com", Platform.APPLE, "test");
        User user2 = new User("bridge2", "bridge2@apple.com", Platform.APPLE, "test2");
        User saveUser2 = userRepository.save(user2);

        Project project1 = Project.builder()
                .title("title1")
                .overview("overview1")
                .user(user2)
                .stage("stage1")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0))
                .build();

        Project saveProject = projectRepository.save(project1);

        ApplyProject applyProject1 = new ApplyProject();
        applyProject1.setUserAndProject(user1, project1);


        user1.getApplyProjects().add(applyProject1);
        User saveUser1 = userRepository.save(user1);

        //when
        projectService.acceptApply(saveProject.getId(), saveUser1.getId());

        //then
        assertEquals("수락", applyProjectRepository.findAll().get(0).getStage());
    }

    @Test
    @DisplayName("프로젝트 거절하기 - 일치하는 경우")
    void rejectApply() {
        //given
        User user1 = new User("bridge1", "bridge1@apple.com", Platform.APPLE, "test");
        User user2 = new User("bridge2", "bridge2@apple.com", Platform.APPLE, "test2");
        User saveUser2 = userRepository.save(user2);

        Project project1 = Project.builder()
                .title("title1")
                .overview("overview1")
                .stage("stage1")
                .user(saveUser2)
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0))
                .build();

        Project saveProject = projectRepository.save(project1);

        ApplyProject applyProject1 = new ApplyProject();
        applyProject1.setUserAndProject(user1, project1);

        user1.getApplyProjects().add(applyProject1);
        User saveUser1 = userRepository.save(user1);

        //when
        projectService.rejectApply(saveProject.getId(), saveUser1.getId());

        //then
        assertEquals("거절", applyProjectRepository.findAll().get(0).getStage());
    }

    @DisplayName("인기글 조회")
    @Test
    void topProjects() {
        // given
        for(int i=1; i<26; i++){
            Project project = projectRepository.save(Project.builder()
                    .title("제목"+i)
                    .dueDate(LocalDateTime.of(2024, 11, i,0,0,0))
                    .build());
            for(int j=i; j<21; j++){
                project.increaseBookmarksNum();
            }
        }
        // when
        List<TopProjectResponseDto> result = projectService.topProjects();

        // then

        Assertions.assertThat(result.size()).isEqualTo(20);
        Assertions.assertThat(result.get(0).getTitle()).isEqualTo("제목1");
        Assertions.assertThat(result.get(19).getTitle()).isEqualTo("제목20");
    }

    @DisplayName("인기글 조회_마감 지난 게시글은 제외")
    @Test
    void topProjects_dateOption() {
        // given
        LocalDateTime localDateTime = LocalDateTime.now();
        int year = localDateTime.getYear();
        int month = localDateTime.getMonthValue();
        int day = localDateTime.getDayOfMonth();

        for(int i=1; i<31; i++){
            Project project = projectRepository.save(Project.builder()
                    .title("제목"+i)
                    .dueDate(LocalDateTime.of(year, month, i,0,0,0))
                    .build());
            for(int j=i; j<31; j++){
                project.increaseBookmarksNum();
            }
            projectRepository.save(project);
        }


        // when
        List<TopProjectResponseDto> result = projectService.topProjects();

        // then
        Assertions.assertThat(result.get(0).getTitle()).isEqualTo("제목"+day);
    }

    @DisplayName("마감 임박 프로젝트 조회 기능")
    @Test
    @Transactional
    void getImminentProjects() {
        // given

        for (int i=0; i<20; i++){
            Project project = Project.builder()
                    .title("project"+(i+1))
                    .dueDate(LocalDateTime.of(2023,9,30-(i%30),0,0,0))
                    .build();
            projectRepository.save(project);

            Project project2 = Project.builder()
                    .title("project"+(i+21))
                    .dueDate(LocalDateTime.of(2024,11,i+1,0,0,0))
                    .build();
            projectRepository.save(project2);
        }

        // when
        List<imminentProjectResponse> responses = projectService.getdeadlineImminentProejcts();

        // then
        Assertions.assertThat(responses.size()).isEqualTo(20);
        Assertions.assertThat(responses.get(0).getDueDate()).isEqualTo(LocalDateTime.of(2024,11,1,0,0,0).toString());
        Assertions.assertThat(responses.get(0).getTitle()).isEqualTo("project21");
        Assertions.assertThat(responses.get(19).getDueDate()).isEqualTo(LocalDateTime.of(2024,11,20,0,0,0).toString());
        Assertions.assertThat(responses.get(19).getTitle()).isEqualTo("project40");
    }

}
