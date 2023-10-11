package com.Bridge.bridge.service;

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
import com.Bridge.bridge.dto.response.ProjectResponseDto;
import com.Bridge.bridge.exception.notfound.NotFoundProjectException;
import com.Bridge.bridge.repository.ApplyProjectRepository;
import com.Bridge.bridge.repository.ProjectRepository;
import com.Bridge.bridge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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

        // When
        List<ProjectListResponseDto> result = projectService.findByTitleAndContent("어플");

        // Then
        assertEquals(result.size(), 4);

    }

    @DisplayName("모집글 생성 기능 test")
    @Test
    void createProject() {
        // given
        User user = new User("bridge", "test1@gmaill.com", Platform.APPLE, "11");
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
                .dueDate("2023-09-07")
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
        User user = new User("bridge", "test1@gmaill.com", Platform.APPLE, "11");
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
                .dueDate("2023-09-07")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .userId(user.getId())
                .stage("Before Start")
                .build();

        projectService.createProject(newProject);

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
        User user1 = new User("bridge", "ImUser@gmail.com", Platform.APPLE, "apple");
        userRepository.save(user1);

        User user2 = new User("bridge2", "NotUser@gmail.com", Platform.APPLE, "google");
        userRepository.save(user2);

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
                .dueDate("2023-09-07")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .userId(user1.getId())
                .stage("Before Start")
                .build();

        projectService.createProject(newProject);

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
        User user = new User("bridge", "update@gmail.com", Platform.APPLE, "updateTest");
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
                .dueDate("2023-09-07")
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
        User user1 = new User("brdige", "update@gmail.com", Platform.APPLE,"updateTest");
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

        // when
        ProjectResponseDto result = projectService.updateProject(wrongId, updateProject);

        // then
        projectRepository.delete(newProject);
    }

    @Test
    @DisplayName("프로젝트 모집글 수정 테스트 _ 잘못된 유저ID")
    void updateProject_NotSameWriterandUser() {
        // given
        User user1 = new User("bridge", "wrongUserID@gmail.com", Platform.APPLE, "updateTest");
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

        // when
        ProjectResponseDto result = projectService.updateProject(newProject.getId(), updateProject);

        // then
        projectRepository.delete(newProject);
    }

    @Test
    @DisplayName("프로젝트 모집글 수정 테스트 _ 프로젝트 작성자 != 유저")
    void updateProject_wrongUserId() {
        // given
        User user1 = new User("bridge", "user1@gmail.com", Platform.APPLE,"updateTest");
        userRepository.save(user1);

        User user2 = new User("bridge2", "user2@gmail.com", Platform.APPLE,"updateTest");
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
                .userId(user1.getId())
                .stage("Before Start")
                .build();

        // when
         ProjectResponseDto result = projectService.updateProject(newProject.getId(), updateProject);


        // then
        projectRepository.delete(newProject);
    }

    @DisplayName("모집글 상세보기 기능")
    @Test
    void detailProject() {
        // given
        User user1 = new User("bridge", "user1@gmail.com", Platform.APPLE, "detailTest");
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
        User user1 = new User("bridge", "detail_Wrong@gmail.com", Platform.APPLE, "detailTest_wrongProjectID");
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
        User user = new User("bridge", "test1@gmaill.com", Platform.APPLE, "apple");
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
        assertEquals("stage1", response.getStage());
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

}