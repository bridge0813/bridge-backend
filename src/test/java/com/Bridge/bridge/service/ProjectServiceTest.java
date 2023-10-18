package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.*;
import com.Bridge.bridge.dto.request.FilterRequestDto;
import com.Bridge.bridge.dto.response.*;
import com.Bridge.bridge.domain.ApplyProject;
import com.Bridge.bridge.domain.Field;
import com.Bridge.bridge.domain.Part;
import com.Bridge.bridge.domain.Platform;
import com.Bridge.bridge.domain.Profile;
import com.Bridge.bridge.domain.Project;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.FilterRequestDto;
import com.Bridge.bridge.dto.response.ApplyProjectResponse;
import com.Bridge.bridge.dto.response.ApplyUserResponse;
import com.Bridge.bridge.dto.response.ProjectListResponseDto;
import com.Bridge.bridge.dto.request.PartRequestDto;
import com.Bridge.bridge.dto.request.ProjectRequestDto;
import com.Bridge.bridge.exception.notfound.NotFoundUserException;
import com.Bridge.bridge.repository.BookmarkRepository;
import com.Bridge.bridge.exception.notfound.NotFoundProjectException;
import com.Bridge.bridge.repository.ApplyProjectRepository;
import com.Bridge.bridge.repository.ProjectRepository;
import com.Bridge.bridge.repository.SearchWordRepository;
import com.Bridge.bridge.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
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

        List<String> skill1 = new ArrayList<>();
        skill1.add("Java");
        skill1.add("Spring boot");

        List<String> skill2 = new ArrayList<>();
        skill2.add("Java");
        skill2.add("Spring boot");

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

        Project newProject1 = Project.builder()
                .title("어플 프로젝트")
                .overview("This is new Project.")
                .dueDate("2023-09-07")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit1)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user)
                .stage("Before Start")
                .build();

        Project newProject2 = Project.builder()
                .title("New project")
                .overview("This is 맛집 어프")
                .dueDate("2023-09-07")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
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
        skill.add("Java");
        skill.add("Spring boot");

        List<PartRequestDto> recruit = new ArrayList<>();
        recruit.add(PartRequestDto.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill)
                .requirement("아무거나")
                .build());

        ProjectRequestDto newProject = ProjectRequestDto.builder()
                .title("New project")
                .overview("This is new Project.")
                .dueDate("230907")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
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

        List<String> skill1 = new ArrayList<>();
        skill1.add("Java");
        skill1.add("Spring boot");

        List<Part> recruit1 = new ArrayList<>();
        recruit1.add(Part.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill1)
                .requirement("아무거나")
                .build());

        Project newProject1 = Project.builder()
                .title("어플 프로젝트")
                .overview("This is new Project.")
                .dueDate("230907")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit1)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user)
                .stage("Before Start")
                .build();

        projectRepository.save(newProject1);


        Long userId = user.getId();
        Long projectId = projectRepository.findByUser_Id(userId).get().getId();

        // when
        Boolean result = projectService.deleteProject(projectId, userId);

        // then
        assertThat(result).isEqualTo(true);
    }

    @DisplayName("프로젝트 삭제 기능 - 삭제하려는 유저가 DB에 없을 때(올바르지 못한 접근)")
    @Test
    void deleteProject_Wrong() {
        // given
        User user1 = new User("delete1", "delete1@gmail.com", Platform.APPLE, "delete1Test");
        userRepository.save(user1);

        User user2 = new User("delete2", "delete2@gmail.com", Platform.APPLE, "delete1Test");
        userRepository.save(user2);

        List<String> skill1 = new ArrayList<>();
        skill1.add("Java");
        skill1.add("Spring boot");

        List<Part> recruit1 = new ArrayList<>();
        recruit1.add(Part.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill1)
                .requirement("아무거나")
                .build());

        Project newProject1 = Project.builder()
                .title("어플 프로젝트")
                .overview("This is new Project.")
                .dueDate("230907")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit1)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user1)
                .stage("Before Start")
                .build();

        projectRepository.save(newProject1);


        Long userId = user1.getId();
        Long projectId = projectRepository.findByUser_Id(userId).get().getId();

        // when
        Boolean result = projectService.deleteProject(projectId, user2.getId());

        // then
        assertThat(result).isEqualTo(false);
    }

    @Test
    @DisplayName("프로젝트 모집글 수정 테스트")
    void updateProject() {
        // given
        User user = new User("update", "update@gmail.com", Platform.APPLE, "updateTest");
        userRepository.save(user);

        List<String> skill = new ArrayList<>();
        skill.add("Java");
        skill.add("Spring boot");

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
                .dueDate("230907")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user)
                .stage("Before Start")
                .build();

        recruit.get(0).setProject(newProject);
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

        ProjectRequestDto updateProject = ProjectRequestDto.builder()
                .title("Update project")
                .overview("This is Updated Project.")
                .dueDate("2023-09-07")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(updateRecruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .userId(user.getId())
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

        List<String> skill = new ArrayList<>();
        skill.add("Java");
        skill.add("Spring boot");

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
                .dueDate("230907")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
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

        ProjectRequestDto updateProject = ProjectRequestDto.builder()
                .title("Update project")
                .overview("This is Updated Project.")
                .dueDate("2023-09-07")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(updateRecruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .userId(user1.getId())
                .stage("Before Start")
                .build();

        Long wrongId = Long.valueOf(123456789);

        // expected
        assertThrows(NotFoundProjectException.class, () -> projectService.updateProject(wrongId, updateProject));
    }

    @Test
    @DisplayName("프로젝트 모집글 수정 테스트 _ 잘못된 유저ID")
    void updateProject_NotSameWriterandUser() {
        // given
        User user1 = new User("update", "update2@gmail.com", Platform.APPLE, "update1Test");
        userRepository.save(user1);

        List<String> skill = new ArrayList<>();
        skill.add("Java");
        skill.add("Spring boot");

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
                .dueDate("2023-09-07")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
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

        Long wrongId = Long.valueOf(123456789);

        ProjectRequestDto updateProject = ProjectRequestDto.builder()
                .title("Update project")
                .overview("This is Updated Project.")
                .dueDate("2023-09-07")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(updateRecruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .userId(wrongId)
                .stage("Before Start")
                .build();

        // expected
        assertThrows(NotFoundUserException.class, () -> projectService.updateProject(newProject.getId(), updateProject));
    }

    @Test
    @DisplayName("프로젝트 모집글 수정 테스트 _ 프로젝트 작성자 != 유저")
    void updateProject_wrongUserId() {
        // given
        User user1 = new User("update1", "update1@gmail.com", Platform.APPLE, "update1Test");
        userRepository.save(user1);

        User user2 = new User("update2", "update2@gmail.com", Platform.APPLE, "update2Test");
        userRepository.save(user2);

        List<String> skill = new ArrayList<>();
        skill.add("Java");
        skill.add("Spring boot");

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
                .dueDate("2023-09-07")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
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

        ProjectRequestDto updateProject = ProjectRequestDto.builder()
                .title("Update project")
                .overview("This is Updated Project.")
                .dueDate("2023-09-07")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(updateRecruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .userId(user2.getId())
                .stage("Before Start")
                .build();

        // expected
         assertThrows(NullPointerException.class, () -> projectService.updateProject(newProject.getId(), updateProject));
    }

    @DisplayName("모집글 상세보기 기능")
    @Test
    void detailProject() {
        // given
        User user1 = new User("find", "find@gmail.com", Platform.APPLE, "find1Test");
        userRepository.save(user1);

        List<String> skill = new ArrayList<>();
        skill.add("Java");
        skill.add("Spring boot");

        List<Part> recruit = new ArrayList<>();
        recruit.add(Part.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill)
                .requirement("아무거나")
                .build());

        Project newProject = Project.builder()
                .title("Find project")
                .overview("This is the project that i find")
                .dueDate("2023-09-07")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user1)
                .stage("Before Start")
                .build();

        Project theProject = projectRepository.save(newProject);

        // when
        ProjectResponseDto result = projectService.getProject(theProject.getId());

        // then
        assertThat(result.getTitle()).isEqualTo(newProject.getTitle());
    }

    @DisplayName("모집글 상세보기 기능 - 잘못된 모집글 Id")
    @Test
    void detailProject_wrongProjectId() {
        // given
        User user1 = new User("detail_wrong", "detail_wrong@gmail.com", Platform.APPLE, "detail_wrongTest");
        userRepository.save(user1);

        List<String> skill = new ArrayList<>();
        skill.add("Java");
        skill.add("Spring boot");

        List<Part> recruit = new ArrayList<>();
        recruit.add(Part.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill)
                .requirement("아무거나")
                .build());

        Project newProject = Project.builder()
                .title("Find project")
                .overview("This is the project that i find")
                .dueDate("2023-09-07")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user1)
                .stage("Before Start")
                .build();

        Project theProject = projectRepository.save(newProject);

        // expected
        assertThrows(NotFoundProjectException.class,
                () -> {
                    ProjectResponseDto result = projectService.getProject(theProject.getId() + Long.valueOf(123));
                });
    }
    
    @DisplayName("모집글 필터링")
    @Test
    void filtering() {
        // given
        User user = new User("user", "user2@gmail.com", Platform.APPLE, "Test");
        userRepository.save(user);

        List<String> skill1 = new ArrayList<>();
        skill1.add("Java");
        skill1.add("Spring boot");

        List<String> skill2 = new ArrayList<>();
        skill2.add("Java");
        skill2.add("Javascript");
        skill2.add("Spring boot");
        skill2.add("NodeJS");

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
                .title("Find project")
                .overview("This is the project that i find")
                .dueDate("2023-09-07")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(new ArrayList<>())
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user)
                .stage("Before Start")
                .build();

        recruit.stream()
                .forEach((part -> part.setProject(newProject1)));

        Project newProject2 = Project.builder()
                .title("Project2")
                .overview("This is new Project2")
                .dueDate("2023-09-17")
                .startDate("2023-09-21")
                .endDate("2023-09-30")
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

        List<String> findSkills = new ArrayList<>();
        findSkills.add("Java");
        findSkills.add("Spring boot");

        FilterRequestDto filterRequestDto = FilterRequestDto.builder()
                .part("backend")
                .skills(findSkills)
                .build();

        // when
        int result = projectService.filterProjectList(filterRequestDto).size();

        // then
        assertThat(result).isEqualTo(1);
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
                .dueDate("23-10-10")
                .build();

        Project project2 = Project.builder()
                .title("title2")
                .overview("overview2")
                .user(user1)
                .stage("stage2")
                .dueDate("23-10-11")
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
                .dueDate("23-10-10")
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
        assertEquals("23-10-10", response.getDueDate());
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
                .dueDate("23-10-10")
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

        List<String> skill1 = new ArrayList<>();
        skill1.add("Java");
        skill1.add("Spring boot");

        List<String> skill2 = new ArrayList<>();
        skill2.add("Java");
        skill2.add("Javascript");
        skill2.add("Spring boot");
        skill2.add("NodeJS");

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
                .dueDate("2023-09-07")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
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
                .dueDate("2023-09-17")
                .startDate("2023-09-21")
                .endDate("2023-09-30")
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

        List<String> skill1 = new ArrayList<>();
        skill1.add("Java");
        skill1.add("Spring boot");

        List<String> skill2 = new ArrayList<>();
        skill2.add("Java");
        skill2.add("Javascript");
        skill2.add("Spring boot");
        skill2.add("NodeJS");

        List<String> skill3 = new ArrayList<>();
        skill2.add("Python");
        skill2.add("Java");
        skill2.add("Spring boot");
        skill2.add("Django");

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
                .dueDate("2023-09-07")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
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
                .dueDate("2023-09-17")
                .startDate("2023-09-21")
                .endDate("2023-09-30")
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
                .dueDate("2023-09-17")
                .startDate("2023-09-21")
                .endDate("2023-09-30")
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

        List<String> skill1 = new ArrayList<>();
        skill1.add("Java");
        skill1.add("Spring boot");

        List<String> skill2 = new ArrayList<>();
        skill2.add("Java");
        skill2.add("Javascript");
        skill2.add("Spring boot");
        skill2.add("NodeJS");

        List<String> skill3 = new ArrayList<>();
        skill2.add("Python");
        skill2.add("Java");
        skill2.add("Spring boot");
        skill2.add("Django");

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
                .dueDate("2023-09-07")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
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
                .dueDate("2023-09-17")
                .startDate("2023-09-21")
                .endDate("2023-09-30")
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
                .dueDate("2023-09-17")
                .startDate("2023-09-21")
                .endDate("2023-09-30")
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

        List<String> skill = new ArrayList<>();
        skill.add("Java");
        skill.add("Spring boot");

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
                .dueDate("20240101235959")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user)
                .stage("Before Start")
                .build();

        recruit.get(0).setProject(newProject);
        projectRepository.save(newProject);

        // when
        ProjectResponseDto response = projectService.closeProject(newProject.getId(), user.getId());

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

        List<String> skill = new ArrayList<>();
        skill.add("Java");
        skill.add("Spring boot");

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
                .dueDate("20230101235959")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user)
                .stage("Before Start")
                .build();

        recruit.get(0).setProject(newProject);
        projectRepository.save(newProject);

        // when
        System.out.println(assertThrows(IllegalStateException.class, ()-> projectService.closeProject(newProject.getId(), user.getId()))
                .getMessage());
    }

    @DisplayName("모집글 스크랩 기능")
    @Test
    void scrap() {
        // given
        User user = new User("scrap", "scrap@gmail.com", Platform.APPLE, "scrapTest");
        user = userRepository.save(user);

        List<String> skill = new ArrayList<>();
        skill.add("Java");
        skill.add("Spring boot");

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
                .dueDate("20230101235959")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
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

        List<String> skill = new ArrayList<>();
        skill.add("Java");
        skill.add("Spring boot");

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
                .dueDate("20230101235959")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
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

    @DisplayName("최근 검색어 조회")
    @Test
    void resentSearchWord() {
        // given
        User user = new User("user1", "user1@gmail.com", Platform.APPLE, "Test");
        user = userRepository.save(user);

        SearchWord newSearch1 = SearchWord.builder()
                .content("검색어1")
                .user(user)
                .history(LocalDateTime.now())
                .build();
        SearchWord newSearch2 = SearchWord.builder()
                .content("검색어2")
                .user(user)
                .history(LocalDateTime.now())
                .build();
        SearchWord newSearch3 = SearchWord.builder()
                .content("검색어3")
                .user(user)
                .history(LocalDateTime.now())
                .build();

        searchWordRepository.save(newSearch1);
        searchWordRepository.save(newSearch2);
        searchWordRepository.save(newSearch3);

        user.getSearchWords().add(newSearch1);
        user.getSearchWords().add(newSearch2);
        user.getSearchWords().add(newSearch3);

        // when
        List<SearchWordResponseDto> searchWordResponseDto = projectService.resentSearchWord(user.getId());
        Assertions.assertThat(searchWordResponseDto.get(0).getSearchWord()).isEqualTo("검색어1");
        Assertions.assertThat(searchWordResponseDto.get(1).getSearchWord()).isEqualTo("검색어2");
        Assertions.assertThat(searchWordResponseDto.get(2).getSearchWord()).isEqualTo("검색어3");
    }

    @DisplayName("최근 검색어 삭제")
    @Test
    void deleteSearchWord() {
        // given
        User user = new User("user1", "user1@gmail.com", Platform.APPLE, "Test");
        user = userRepository.save(user);

        SearchWord newSearch1 = SearchWord.builder()
                .content("검색어1")
                .user(user)
                .history(LocalDateTime.now())
                .build();
        SearchWord newSearch2 = SearchWord.builder()
                .content("검색어2")
                .user(user)
                .history(LocalDateTime.now())
                .build();
        SearchWord newSearch3 = SearchWord.builder()
                .content("검색어3")
                .user(user)
                .history(LocalDateTime.now())
                .build();

        searchWordRepository.save(newSearch1);
        searchWordRepository.save(newSearch2);
        searchWordRepository.save(newSearch3);

        user.getSearchWords().add(newSearch1);
        user.getSearchWords().add(newSearch2);
        user.getSearchWords().add(newSearch3);

        // when
        List<SearchWordResponseDto> searchWordResponseDto = projectService.deleteSearchWord(user.getId(), newSearch1.getId());
        Assertions.assertThat(searchWordResponseDto.get(0).getSearchWord()).isEqualTo("검색어2");
        Assertions.assertThat(searchWordResponseDto.get(1).getSearchWord()).isEqualTo("검색어3");
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
                .dueDate("23-10-10")
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

        Field field1 = new Field("Backend");
        field1.updateFieldUser(user1);

        Field field2 = new Field("Frontend");
        field2.updateFieldUser(user2);

        user1.getFields().add(field1);
        user2.getFields().add(field2);

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
                .dueDate("23-10-10")
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
    @DisplayName("프로젝트 지원자 목록 - 지원자 내용 확인")
    void getApplyUsersDetail() {
        //given
        User user1 = new User("bridge1", "bridge1@apple.com", Platform.APPLE, "1");

        Field field1 = new Field("Backend");
        field1.updateFieldUser(user1);

        user1.getFields().add(field1);

        Profile profile1 = Profile.builder()
                .career("career1")
                .build();

        user1.updateProfile(profile1);

        userRepository.save(user1);

        Project project1 = Project.builder()
                .title("title1")
                .overview("overview1")
                .stage("stage1")
                .dueDate("23-10-10")
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
        assertEquals("Backend", response.getFields().get(0));
        assertEquals("career1", response.getCareer());
    }

    @Test
    @DisplayName("프로젝트 수락하기")
    void acceptApply() {
        //given
        User user1 = new User("bridge1", "bridge1@apple.com", Platform.APPLE, "test");

        Project project1 = Project.builder()
                .title("title1")
                .overview("overview1")
                .stage("stage1")
                .dueDate("23-10-10")
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
    @DisplayName("프로젝트 거절하기")
    void rejectApply() {
        //given
        User user1 = new User("bridge1", "bridge1@apple.com", Platform.APPLE, "test");

        Project project1 = Project.builder()
                .title("title1")
                .overview("overview1")
                .stage("stage1")
                .dueDate("23-10-10")
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
                    .dueDate(LocalDateTime.of(2023, 11, i,0,0,0).toString())
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
        int year = localDateTime.getYear();;
        int month = localDateTime.getMonthValue();
        int day = localDateTime.getDayOfMonth();

        for(int i=1; i<32; i++){
            Project project = projectRepository.save(Project.builder()
                    .title("제목"+i)
                    .dueDate(LocalDateTime.of(year, month, i,0,0,0).toString())
                    .build());
            for(int j=i; j<31; j++){
                project.increaseBookmarksNum();
            }
            projectRepository.save(project);
        }


        // when
        List<TopProjectResponseDto> result = projectService.topProjects();

        // then

        Assertions.assertThat(result.size()).isEqualTo(32-day);
        Assertions.assertThat(result.get(0).getTitle()).isEqualTo("제목"+day);
        Assertions.assertThat(result.get(31-day).getTitle()).isEqualTo("제목31");
    }
}