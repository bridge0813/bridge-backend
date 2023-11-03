package com.Bridge.bridge.controller;

import com.Bridge.bridge.domain.*;
import com.Bridge.bridge.domain.Profile;
import com.Bridge.bridge.domain.Project;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.FilterRequestDto;
import com.Bridge.bridge.dto.request.PartRequestDto;
import com.Bridge.bridge.dto.request.ProjectRequestDto;
import com.Bridge.bridge.dto.request.ProjectUpdateRequestDto;
import com.Bridge.bridge.repository.ProjectRepository;
import com.Bridge.bridge.repository.SearchWordRepository;
import com.Bridge.bridge.repository.UserRepository;
import com.Bridge.bridge.security.JwtTokenProvider;
import com.Bridge.bridge.service.ProjectService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    SearchWordRepository searchWordRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    @DisplayName("모집글 생성")
    void createProject() throws Exception {
        // given
        User user = new User("user", "user@gmail.com", Platform.APPLE, "Test");
        User newUser = userRepository.save(user);

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

        String body = objectMapper.writeValueAsString(newProject);

        // when
        mockMvc.perform(post("/project")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isOk()) // 응답 status를 ok로 테스트
                .andDo(print());

    }

    @Test
    @DisplayName("모집글 삭제")
    void deleteProject() throws Exception {
        // given
        User user =new User("user", "user@gmail.com", Platform.APPLE, "Test");
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

        Long projectId = projectService.createProject(newProject);

        // when
        mockMvc.perform(delete("/project")
                        .param("projectId", String.valueOf(projectId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(202)) // 응답 status를 ok로 테스트
                .andDo(print());

    }

    @Test
    @DisplayName("모집글 수정")
    void updateProject() throws Exception {
        // given
        User user = new User("user", "user@gmail.com", Platform.APPLE, "Test");
        User saveUser = userRepository.save(user);

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

        Long projectId = projectService.createProject(newProject);

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
                .dueDate(LocalDateTime.of(2023,11,1,0,0,0))
                .startDate(LocalDateTime.of(2023,11,1,0,0,0))
                .endDate(LocalDateTime.of(2023,11,1,0,0,0))
                .recruit(updateRecruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .stage("Before Start")
                .build();

        //when
        mockMvc.perform(put("/project")
                        .param("projectId", String.valueOf(projectId))
                        .content(objectMapper.writeValueAsString(updateProject))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200)) // 응답 status를 ok로 테스트
                .andDo(print());
    }

    @Test
    @DisplayName("모집글 상세보기")
    void detailProject() throws Exception {
        // given
        User user1 = new User("user", "user@gmail.com", Platform.APPLE, "Test");
        User saveUser = userRepository.save(user1);

        String token = Jwts.builder()
                .setSubject(String.valueOf(saveUser.getId()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

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
                .title("Find project")
                .overview("This is the project that i find")
                .dueDate(LocalDateTime.of(2023,11,1,0,0,0))
                .startDate(LocalDateTime.of(2023,11,1,0,0,0))
                .endDate(LocalDateTime.of(2023,11,1,0,0,0))
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user1)
                .stage("Before Start")
                .build();

        Project theProject = projectRepository.save(newProject);


        // when
        mockMvc.perform(get("/project")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("projectId", String.valueOf(theProject.getId())))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(newProject.getTitle()))
                .andDo(print());

    }

    @Test
    @DisplayName("필터링")
    void filtering() throws Exception {
        // given
        User user = new User("user", "user@gmail.com", Platform.APPLE, "Test");
        userRepository.save(user);

        List<String> skill1 = new ArrayList<>();
        skill1.add("JAVA");
        skill1.add("SPRINGBOOT");

        List<String> skill2 = new ArrayList<>();
        skill2.add("JAVA");
        skill2.add("SPRINGBOOT");

        List<PartRequestDto> recruit1 = new ArrayList<>();
        recruit1.add(PartRequestDto.builder()
                .recruitPart("Backend")
                .recruitNum(3)
                .recruitSkill(skill1)
                .requirement("Backend")
                .build());

        List<PartRequestDto> recruit2 = new ArrayList<>();
        recruit2.add(PartRequestDto.builder()
                .recruitPart("frontend")
                .recruitNum(1)
                .recruitSkill(skill2)
                .requirement("frontend")
                .build());

        ProjectRequestDto newProject1 = ProjectRequestDto.builder()
                .title("This is what i find")
                .overview("This is backend Project.")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0).toString())
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit1)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .userId(user.getId())
                .stage("Before Start")
                .build();

        ProjectRequestDto newProject2 = ProjectRequestDto.builder()
                .title("This is not what i find")
                .overview("This is frontend Project.")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0).toString())
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit2)
                .tagLimit(new ArrayList<>())
                .meetingWay("ONline")
                .userId(user.getId())
                .stage("Before Start")
                .build();

        projectService.createProject(newProject1);
        projectService.createProject(newProject2);

        List<String> findSkills = new ArrayList<>();
        findSkills.add("JAVA");
        findSkills.add("SPRINGBOOT");

        FilterRequestDto filterRequestDto = FilterRequestDto.builder()
                .part("Backend")
                .skills(findSkills)
                .build();

        String body = objectMapper.writeValueAsString(filterRequestDto);

        // when
        String expectByTitle = "$.[?(@.title == '%s')]";

        mockMvc.perform(post("/projects/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("This is what i find"))
                .andDo(print());

    }

    @DisplayName("내가 작성한 모집글들 불러오기")
    @Test
    void findMyProjects() throws Exception {
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

        // expected
        mockMvc.perform(get("/projects/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(user.getId())))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Find MyProject1"))
                .andDo(print());

    }

    @DisplayName("모든 모집글들 불러오기")
    @Test
    void allProjects() throws Exception {
        // given
        User user1 = new User("user1", "user@gmail.com", Platform.APPLE, "Test");
        userRepository.save(user1);

        User user2 = new User("user2", "user@gmail.com", Platform.APPLE, "Test");
        userRepository.save(user2);

        User user3 = new User("user3", "user@gmail.com", Platform.APPLE, "Test");
        userRepository.save(user3);

        List<String> skill1 = new ArrayList<>();
        skill1.add("JAVA");
        skill1.add("SPRINGBOOT");

        List<String> skill2 = new ArrayList<>();
        skill2.add("JAVA");
        skill2.add("SPRINGBOOT");

        List<String> skill3 = new ArrayList<>();
        skill3.add("PYTHON");
        skill3.add("DJANGO");

        List<PartRequestDto> recruit1 = new ArrayList<>();
        recruit1.add(PartRequestDto.builder()
                .recruitPart("Backend")
                .recruitNum(3)
                .recruitSkill(skill1)
                .requirement("Backend")
                .build());

        List<PartRequestDto> recruit2 = new ArrayList<>();
        recruit2.add(PartRequestDto.builder()
                .recruitPart("Frontend")
                .recruitNum(1)
                .recruitSkill(skill2)
                .requirement("Frontend")
                .build());

        List<PartRequestDto> recruit3 = new ArrayList<>();
        recruit3.add(PartRequestDto.builder()
                .recruitPart("Backtend")
                .recruitNum(5)
                .recruitSkill(skill3)
                .requirement("Backend")
                .build());

        ProjectRequestDto newProject1 = ProjectRequestDto.builder()
                .title("Myproject1")
                .overview("This is Myproject1")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0).toString())
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit1)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .userId(user1.getId())
                .stage("Before Start")
                .build();

        ProjectRequestDto newProject2 = ProjectRequestDto.builder()
                .title("Myproject2")
                .overview("This is Myproject2")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0).toString())
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit2)
                .tagLimit(new ArrayList<>())
                .meetingWay("ONline")
                .userId(user2.getId())
                .stage("Before Start")
                .build();

        ProjectRequestDto newProject3 = ProjectRequestDto.builder()
                .title("Myproject3")
                .overview("This is Myproject3")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0).toString())
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit3)
                .tagLimit(new ArrayList<>())
                .meetingWay("ONline")
                .userId(user3.getId())
                .stage("Before Start")
                .build();

        projectService.createProject(newProject1);
        projectService.createProject(newProject2);
        projectService.createProject(newProject3);

        // when
        String expectByTitle = "$.[?(@.title == '%s')]";

        mockMvc.perform(get("/projects/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(expectByTitle, "Myproject3").exists())
                .andDo(print());

    }

    @Test
    @DisplayName("내 분야 모집글 불러오기")
    void findMypartProjects() throws Exception {
        // given
        User user = new User("user", "user@gmail.com", Platform.APPLE, "Test");
        userRepository.save(user);

        List<String> skill1 = new ArrayList<>();
        skill1.add("JAVA");
        skill1.add("SPRINGBOOT");

        List<String> skill2 = new ArrayList<>();
        skill2.add("JAVA");
        skill2.add("SPRINGBOOT");

        List<PartRequestDto> recruit1 = new ArrayList<>();
        recruit1.add(PartRequestDto.builder()
                .recruitPart("Backend")
                .recruitNum(3)
                .recruitSkill(skill1)
                .requirement("Backend")
                .build());

        List<PartRequestDto> recruit2 = new ArrayList<>();
        recruit2.add(PartRequestDto.builder()
                .recruitPart("Frontend")
                .recruitNum(1)
                .recruitSkill(skill2)
                .requirement("Frontend")
                .build());

        List<PartRequestDto> recruit3 = new ArrayList<>();
        recruit3.add(PartRequestDto.builder()
                .recruitPart("Backend")
                .recruitNum(1)
                .recruitSkill(skill2)
                .requirement("Backend")
                .build());

        ProjectRequestDto newProject1 = ProjectRequestDto.builder()
                .title("This is backend Project.")
                .overview("This is backend Project.")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0).toString())
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit1)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .userId(user.getId())
                .stage("Before Start")
                .build();

        ProjectRequestDto newProject2 = ProjectRequestDto.builder()
                .title("This is not what i find")
                .overview("This is frontend Project.")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0).toString())
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit2)
                .tagLimit(new ArrayList<>())
                .meetingWay("ONline")
                .userId(user.getId())
                .stage("Before Start")
                .build();

        ProjectRequestDto newProject3 = ProjectRequestDto.builder()
                .title("This is backend Project.")
                .overview("This is backend Project.")
                .dueDate(LocalDateTime.of(2024,1,12,0,0,0).toString())
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit3)
                .tagLimit(new ArrayList<>())
                .meetingWay("ONline")
                .userId(user.getId())
                .stage("Before Start")
                .build();

        projectService.createProject(newProject1);
        projectService.createProject(newProject2);
        projectService.createProject(newProject3);

        // when
        String expectByTitle = "$.[?(@.title == '%s')]";

        mockMvc.perform(post("/projects/mypart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("backend"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(expectByTitle, "This is backend Project.").exists())
                .andDo(print());

    }

    @Test
    @DisplayName("모집글 마감하기")
    void closeProject() throws Exception {
        // given
        User user1 = new User("closeProject", "closeProject@gmail.com", Platform.APPLE, "closeProjectTest");
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

        Project newProject = Project.builder()
                .title("Find project")
                .overview("This is the project that i find")
                .dueDate(LocalDateTime.of(2024,11,1,0,0,0))
                .startDate(LocalDateTime.of(2023,11,1,0,0,0))
                .endDate(LocalDateTime.of(2023,11,1,0,0,0))
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user1)
                .stage("Before Start")
                .build();

        Project theProject = projectRepository.save(newProject);

        Long projectId = theProject.getId();

        // when
        mockMvc.perform(post("/project/deadline")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(newProject.getTitle()))
                .andDo(print());

    }

    @Test
    @DisplayName("모집글 스크랩")
    void scrap() throws Exception {
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
                .dueDate(LocalDateTime.of(2024,11,1,0,0,0))
                .startDate(LocalDateTime.of(2023,11,1,0,0,0))
                .endDate(LocalDateTime.of(2023,11,1,0,0,0))
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user)
                .stage("Before Start")
                .build();

        recruit.get(0).setProject(newProject);
        newProject = projectRepository.save(newProject);

        Long projectId = newProject.getId();
        Long userId = user.getId();

        // when
        mockMvc.perform(post("/project/scrap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userId))
                        .content(objectMapper.writeValueAsString(projectId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.scrap").value("스크랩이 설정되었습니다."))
                .andDo(print());

    }


    @Test
    @DisplayName("지원한 프로젝트 목록 조회")
    void getApplyProjects() throws Exception {
        //given
        User user1 = new User("bridge1", "bridge1@apple.com", Platform.APPLE, "1");

        Project project1 = Project.builder()
                .title("title1")
                .overview("overview1")
                .stage("stage1")
                .dueDate(LocalDateTime.of(2024,11,1,0,0,0))
                .build();

        projectRepository.save(project1);

        ApplyProject applyProject1 = new ApplyProject();
        applyProject1.setUserAndProject(user1, project1);

        user1.getApplyProjects().add(applyProject1);
        User saveUser1 = userRepository.save(user1);

        //expected
        mockMvc.perform(get("/projects/apply")
                    .param("userId", saveUser1.getId().toString())
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stage").value("결과 대기중"))
                .andExpect(jsonPath("$[0].title").value("title1"))
                .andExpect(jsonPath("$[0].overview").value("overview1"))
                .andExpect(jsonPath("$[0].dueDate").value(LocalDateTime.of(2024,11,1,0,0,0).toString()))
                .andDo(print());
    }

    @Test
    @DisplayName("프로젝트 지원하기")
    void applyProjects() throws Exception {
        //given
        User user1 = new User("bridge1", "bridge1@apple.com", Platform.APPLE, "1");
        user1.updateDeviceToken("deviceToken");
        User saveUser1 = userRepository.save(user1);

        Project project1 = Project.builder()
                .title("title1")
                .overview("overview1")
                .stage("stage1")
                .dueDate(LocalDateTime.of(2024,11,1,0,0,0))
                .user(user1)
                .build();

        Project saveProject = projectRepository.save(project1);

        //expected
        mockMvc.perform(post("/projects/apply")
                        .param("userId", String.valueOf(saveUser1.getId()))
                        .param("projectId", String.valueOf(saveProject.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("프로젝트 지원 취소하기")
    void cancelApply() throws Exception {
        //given
        User user1 = new User("device Token");

        Project project1 = Project.builder()
                .title("title1")
                .overview("overview1")
                .stage("stage1")
                .dueDate(LocalDateTime.of(2024,11,1,0,0,0))
                .build();

        Project saveProject = projectRepository.save(project1);

        ApplyProject applyProject1 = new ApplyProject();
        applyProject1.setUserAndProject(user1, project1);

        user1.getApplyProjects().add(applyProject1);
        User saveUser1 = userRepository.save(user1);

        //expected
        mockMvc.perform(post("/projects/apply/cancel")
                        .param("userId", saveUser1.getId().toString())
                        .param("projectId", saveProject.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("프로젝트 지원자 목록")
    void getApplyUsersDetail() throws Exception {
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
                .dueDate(LocalDateTime.of(2023,11,1,0,0,0))
                .build();

        ApplyProject applyProject1 = new ApplyProject();
        applyProject1.setUserAndProject(user1, project1);

        project1.getApplyProjects().add(applyProject1);
        Project saveProject = projectRepository.save(project1);

        //expected
        mockMvc.perform(get("/projects/apply/users")
                        .param("projectId", saveProject.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(user1.getId()))
                .andExpect(jsonPath("$[0].name").value("bridge1"))
                .andExpect(jsonPath("$[0].fields[0]").value("백엔드"))
                .andExpect(jsonPath("$[0].career").value("career1"))
                .andDo(print());
    }

    @Test
    @DisplayName("프로젝트 수락하기")
    void acceptApply() throws Exception {
        //given
        User user1 = new User("device Token");
        userRepository.save(user1);

        Project project1 = Project.builder()
                .title("title1")
                .overview("overview1")
                .stage("stage1")
                .dueDate(LocalDateTime.of(2023,11,1,0,0,0))
                .user(user1)
                .build();

        Project saveProject = projectRepository.save(project1);

        ApplyProject applyProject1 = new ApplyProject();
        applyProject1.setUserAndProject(user1, project1);


        user1.getApplyProjects().add(applyProject1);
        User saveUser1 = userRepository.save(user1);

        //expected
        mockMvc.perform(put("/projects/accept")
                        .param("projectId", saveProject.getId().toString())
                        .param("userId", saveUser1.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("프로젝트 거절하기")
    void rejectApply() throws Exception {
        //given
        User user1 = new User("device Token");
        userRepository.save(user1);

        Project project1 = Project.builder()
                .title("title1")
                .overview("overview1")
                .stage("stage1")
                .dueDate(LocalDateTime.of(2023,11,1,0,0,0))
                .user(user1)
                .build();

        Project saveProject = projectRepository.save(project1);

        ApplyProject applyProject1 = new ApplyProject();
        applyProject1.setUserAndProject(user1, project1);


        user1.getApplyProjects().add(applyProject1);
        User saveUser1 = userRepository.save(user1);

        //expected
        mockMvc.perform(put("/projects/reject")
                        .param("projectId", saveProject.getId().toString())
                        .param("userId", saveUser1.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true))
                .andDo(print());
    }
  
    @DisplayName("인기글 조회 기능")
    @Test
    void topProjects() throws Exception{
        // given
        LocalDateTime localDateTime = LocalDateTime.now();
        int year = localDateTime.getYear();;
        int month = localDateTime.getMonthValue();
        int day = localDateTime.getDayOfMonth();

        for(int i=1; i<30; i++){
            Project project = projectRepository.save(Project.builder()
                    .title("제목"+i)
                    .dueDate(LocalDateTime.of(year, month, i,0,0,0))
                    .build());
            for(int j=i; j<31; j++){
                project.increaseBookmarksNum();
            }
            projectRepository.save(project);
        }

        //expected
        mockMvc.perform(get("/projects/top")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("제목"+day))
                .andDo(print());
    }

    @DisplayName("마감 임박 모집글 조회 기능")
    @Test
    void imminentProjects() throws Exception{
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

        //expected
        mockMvc.perform(get("/projects/imminent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("project21"))
                .andExpect(jsonPath("$[0].dueDate").value(LocalDateTime.of(2024,11,1,0,0,0).toString()))
                .andExpect(jsonPath("$[19].title").value("project40"))
                .andExpect(jsonPath("$[19].dueDate").value(LocalDateTime.of(2024,11,20,0,0,0).toString()))
                .andDo(print());
    }
}