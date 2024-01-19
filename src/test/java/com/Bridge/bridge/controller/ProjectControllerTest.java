package com.Bridge.bridge.controller;

import com.Bridge.bridge.domain.*;
import com.Bridge.bridge.domain.Profile;
import com.Bridge.bridge.domain.Project;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.FilterRequest;
import com.Bridge.bridge.dto.request.PartRequest;
import com.Bridge.bridge.dto.request.ProjectRequest;
import com.Bridge.bridge.dto.request.ProjectUpdateRequest;
import com.Bridge.bridge.repository.BookmarkRepository;
import com.Bridge.bridge.repository.ProjectRepository;
import com.Bridge.bridge.repository.SearchWordRepository;
import com.Bridge.bridge.repository.UserRepository;
import com.Bridge.bridge.security.JwtTokenProvider;
import com.Bridge.bridge.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
    BookmarkRepository bookmarkRepository;

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
        User user = new User("user", Platform.APPLE, "Test");
        User newUser = userRepository.save(user);

        List<String> skill = new ArrayList<>();
        skill.add("Java");
        skill.add("Spring boot");

        List<PartRequest> recruit = new ArrayList<>();
        recruit.add(PartRequest.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill)
                .requirement("아무거나")
                .build());

        LocalDateTime dueDate = LocalDateTime.of(2050,1,12,0,0,0);
        LocalDateTime startDate = LocalDateTime.of(2023,2,12,0,0,0);
        LocalDateTime endDate = LocalDateTime.of(2023,3,12,0,0,0);

        ProjectRequest newProject = ProjectRequest.builder()
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
        User user =new User("user", Platform.APPLE, "Test");
        userRepository.save(user);

        List<String> skill = new ArrayList<>();
        skill.add("JAVA");
        skill.add("SPRINGBOOT");

        List<PartRequest> recruit = new ArrayList<>();
        recruit.add(PartRequest.builder()
                .recruitPart("BACKEND")
                .recruitNum(3)
                .recruitSkill(skill)
                .requirement("아무거나")
                .build());

        LocalDateTime dueDate = LocalDateTime.of(2050,1,12,0,0,0);
        LocalDateTime startDate = LocalDateTime.of(2023,2,12,0,0,0);
        LocalDateTime endDate = LocalDateTime.of(2023,3,12,0,0,0);

        ProjectRequest newProject = ProjectRequest.builder()
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
        User user = new User("user", Platform.APPLE, "Test");
        User saveUser = userRepository.save(user);

        List<String> skill = new ArrayList<>();
        skill.add("JAVA");
        skill.add("SPRINGBOOT");

        List<PartRequest> recruit = new ArrayList<>();
        recruit.add(PartRequest.builder()
                .recruitPart("BACKEND")
                .recruitNum(3)
                .recruitSkill(skill)
                .requirement("아무거나")
                .build());

        LocalDateTime dueDate = LocalDateTime.of(2050,1,12,0,0,0);
        LocalDateTime startDate = LocalDateTime.of(2023,2,12,0,0,0);
        LocalDateTime endDate = LocalDateTime.of(2023,3,12,0,0,0);

        ProjectRequest newProject = ProjectRequest.builder()
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

        List<PartRequest> updateRecruit = new ArrayList<>();
        updateRecruit.add(PartRequest.builder()
                .recruitPart("FRONTEND")
                .recruitNum(2)
                .recruitSkill(updateSkill)
                .requirement("화이팅")
                .build());

        ProjectUpdateRequest updateProject = ProjectUpdateRequest.builder()
                .title("Update project")
                .overview("This is Updated Project.")
                .dueDate(LocalDateTime.of(2050,11,1,0,0,0))
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
        User user1 = new User("user", Platform.APPLE, "Test");
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
                .recruitPart(Field.BACKEND)
                .recruitNum(3)
                .recruitSkill(skill)
                .requirement("아무거나")
                .build());

        Project newProject = Project.builder()
                .title("Find project")
                .overview("This is the project that i find")
                .dueDate(LocalDateTime.of(2050,11,1,0,0,0))
                .startDate(LocalDateTime.of(2023,11,1,0,0,0))
                .endDate(LocalDateTime.of(2023,11,1,0,0,0))
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user1)
                .stage("Before Start")
                .build();

        recruit.get(0).setProject(newProject);
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
        User user = new User("user", Platform.APPLE, "Test");
        userRepository.save(user);

        List<String> skill1 = new ArrayList<>();
        skill1.add("JAVA");
        skill1.add("SPRINGBOOT");

        List<String> skill2 = new ArrayList<>();
        skill2.add("JAVA");
        skill2.add("SPRINGBOOT");

        List<PartRequest> recruit1 = new ArrayList<>();
        recruit1.add(PartRequest.builder()
                .recruitPart("BACKEND")
                .recruitNum(3)
                .recruitSkill(skill1)
                .requirement("Backend")
                .build());

        List<PartRequest> recruit2 = new ArrayList<>();
        recruit2.add(PartRequest.builder()
                .recruitPart("FRONTEND")
                .recruitNum(1)
                .recruitSkill(skill2)
                .requirement("frontend")
                .build());

        ProjectRequest newProject1 = ProjectRequest.builder()
                .title("This is what i find")
                .overview("This is backend Project.")
                .dueDate(LocalDateTime.of(2050,11,1,0,0,0).toString())
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit1)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .userId(user.getId())
                .stage("Before Start")
                .build();

        ProjectRequest newProject2 = ProjectRequest.builder()
                .title("This is not what i find")
                .overview("This is frontend Project.")
                .dueDate(LocalDateTime.of(2050,1,12,0,0,0).toString())
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

        FilterRequest filterRequest = FilterRequest.builder()
                .part("BACKEND")
                .skills(findSkills)
                .build();

        String body = objectMapper.writeValueAsString(filterRequest);

        String token = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        // when
        mockMvc.perform(post("/projects/category")
                        .header("Authorization", "Bearer " + token)
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
        User user = new User("user", Platform.APPLE, "Test");
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
                .recruitPart(Field.BACKEND)
                .recruitNum(3)
                .recruitSkill(skill1)
                .requirement("아무거나")
                .build());

        List<Part> recruit2 = new ArrayList<>();
        recruit2.add(Part.builder()
                .recruitPart(Field.FRONTEND)
                .recruitNum(1)
                .recruitSkill(skill2)
                .requirement("skill2")
                .build());

        Project newProject1 = Project.builder()
                .title("Find MyProject1")
                .overview("This is My Project1")
                .dueDate(LocalDateTime.of(2050,11,1,0,0,0))
                .startDate(LocalDateTime.of(2024,2,12,0,0,0))
                .endDate(LocalDateTime.of(2024,3,12,0,0,0))
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
                .dueDate(LocalDateTime.of(2050,11,1,0,0,0))
                .startDate(LocalDateTime.of(2024,1,12,0,0,0))
                .endDate(LocalDateTime.of(2024,1,12,0,0,0))
                .tagLimit(new ArrayList<>())
                .meetingWay("ONline")
                .user(user)
                .stage("Before Start")
                .build();

        recruit2.stream()
                .forEach((part -> part.setProject(newProject2)));

        projectRepository.save(newProject1);
        projectRepository.save(newProject2);

        String token = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        // expected
        mockMvc.perform(get("/projects/")
                        .header("Authorization", "Bearer " + token)
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
        User user1 = new User("user1", Platform.APPLE, "Test");
        userRepository.save(user1);

        User user2 = new User("user2", Platform.APPLE, "Test");
        userRepository.save(user2);

        User user3 = new User("user3", Platform.APPLE, "Test");
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
                .recruitPart(Field.BACKEND)
                .recruitNum(3)
                .recruitSkill(skill1)
                .requirement("아무거나")
                .build());

        List<Part> recruit2 = new ArrayList<>();
        recruit2.add(Part.builder()
                .recruitPart(Field.FRONTEND)
                .recruitNum(1)
                .recruitSkill(skill2)
                .requirement("skill2")
                .build());

        List<Part> recruit3 = new ArrayList<>();
        recruit3.add(Part.builder()
                .recruitPart(Field.BACKEND)
                .recruitNum(5)
                .recruitSkill(skill3)
                .requirement("skill3")
                .build());

        Project newProject1 = Project.builder()
                .title("Find AllProject1")
                .overview("This is My Project1")
                .dueDate(LocalDateTime.of(2050,11,1,0,0,0))
                .startDate(LocalDateTime.of(2024,1,12,0,0,0))
                .endDate(LocalDateTime.of(2024,1,12,0,0,0))
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
                .dueDate(LocalDateTime.of(2050,11,1,0,0,0))
                .startDate(LocalDateTime.of(2024,1,12,0,0,0))
                .endDate(LocalDateTime.of(2024,1,12,0,0,0))
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
                .dueDate(LocalDateTime.of(2050,11,1,0,0,0))
                .startDate(LocalDateTime.of(2024,1,12,0,0,0))
                .endDate(LocalDateTime.of(2024,1,12,0,0,0))
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

        Bookmark bookmark1 = Bookmark.builder()
                .project(newProject2)
                .user(user1)
                .build();
        bookmarkRepository.save(bookmark1);
        user1.getBookmarks().add(bookmark1);
        newProject2.getBookmarks().add(bookmark1);

        Bookmark bookmark2 = Bookmark.builder()
                .project(newProject3)
                .user(user1)
                .build();
        bookmarkRepository.save(bookmark2);
        user1.getBookmarks().add(bookmark2);
        newProject3.getBookmarks().add(bookmark1);

        // when
        String expectByTitle = "$.[?(@.title == '%s')]";

        mockMvc.perform(get("/projects/all")
//                        .param("userId", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(expectByTitle, "Find AllProject3").exists())
                .andDo(print());

    }

    @Test
    @DisplayName("내 분야 모집글 불러오기")
    @Transactional
    void findMypartProjects() throws Exception {
        // given
        User user = new User("user", Platform.APPLE, "Test");
        User user2 = new User("user2", Platform.APPLE, "Test");
        userRepository.save(user);
        userRepository.save(user2);

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
                .recruitPart(Field.BACKEND)
                .recruitNum(3)
                .recruitSkill(skill1)
                .requirement("아무거나")
                .build());

        List<Part> recruit2 = new ArrayList<>();
        recruit2.add(Part.builder()
                .recruitPart(Field.FRONTEND)
                .recruitNum(1)
                .recruitSkill(skill2)
                .requirement("skill2")
                .build());

        List<Part> recruit3 = new ArrayList<>();
        recruit3.add(Part.builder()
                .recruitPart(Field.BACKEND)
                .recruitNum(5)
                .recruitSkill(skill3)
                .requirement("skill3")
                .build());

        Project newProject1 = Project.builder()
                .title("Find AllProject1")
                .overview("This is My Project1")
                .dueDate(LocalDateTime.of(2050,11,1,0,0,0))
                .startDate(LocalDateTime.of(2024,1,12,0,0,0))
                .endDate(LocalDateTime.of(2024,1,12,0,0,0))
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user)
                .stage("Before Start")
                .build();

        recruit.stream()
                .forEach((part -> part.setProject(newProject1)));

        Project newProject2 = Project.builder()
                .title("Find AllProject2")
                .overview("This is My Project2")
                .dueDate(LocalDateTime.of(2050,11,1,0,0,0))
                .startDate(LocalDateTime.of(2024,1,12,0,0,0))
                .endDate(LocalDateTime.of(2024,1,12,0,0,0))
                .tagLimit(new ArrayList<>())
                .meetingWay("ONline")
                .user(user)
                .stage("Before Start")
                .build();

        recruit2.stream()
                .forEach((part -> part.setProject(newProject2)));

        Project newProject3 = Project.builder()
                .title("Find AllProject3")
                .overview("This is My Project3")
                .dueDate(LocalDateTime.of(2050,11,1,0,0,0))
                .startDate(LocalDateTime.of(2024,1,12,0,0,0))
                .endDate(LocalDateTime.of(2024,1,12,0,0,0))
                .tagLimit(new ArrayList<>())
                .meetingWay("ONline")
                .user(user)
                .stage("Before Start")
                .build();

        recruit3.stream()
                .forEach((part -> part.setProject(newProject3)));

        projectRepository.save(newProject1);
        projectRepository.save(newProject2);
        projectRepository.save(newProject3);

        Bookmark bookmark1 = Bookmark.builder()
                .project(newProject2)
                .user(user2)
                .build();
        bookmarkRepository.save(bookmark1);
        user2.getBookmarks().add(bookmark1);
        newProject2.getBookmarks().add(bookmark1);

        Bookmark bookmark2 = Bookmark.builder()
                .project(newProject3)
                .user(user2)
                .build();
        bookmarkRepository.save(bookmark2);

        String token = Jwts.builder()
                .setSubject(String.valueOf(user2.getId()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        // when
        String expectByTitle = "$.[?(@.title == '%s')]";
        JSONObject body = new JSONObject();
        body.appendField("part", "BACKEND");

        mockMvc.perform(post("/projects/mypart")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(expectByTitle, "Find AllProject1").exists())
                .andExpect(jsonPath(expectByTitle, "Find AllProject3").exists())
                .andExpect(jsonPath("$[0].scrap").value(false))
                .andExpect(jsonPath("$[1].scrap").value(true))
                .andDo(print());

    }

    @Test
    @DisplayName("모집글 마감하기")
    void closeProject() throws Exception {
        // given
        User user1 = new User("closeProject", Platform.APPLE, "closeProjectTest");
        userRepository.save(user1);

        List<Stack> skill = new ArrayList<>();
        skill.add(Stack.JAVA);
        skill.add(Stack.SPRINGBOOT);

        List<Part> recruit = new ArrayList<>();
        recruit.add(Part.builder()
                .recruitPart(Field.BACKEND)
                .recruitNum(3)
                .recruitSkill(skill)
                .requirement("아무거나")
                .build());

        Project newProject = Project.builder()
                .title("Find project")
                .overview("This is the project that i find")
                .dueDate(LocalDateTime.of(2050,11,1,0,0,0))
                .startDate(LocalDateTime.of(2023,11,1,0,0,0))
                .endDate(LocalDateTime.of(2023,11,1,0,0,0))
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user1)
                .stage("Before Start")
                .build();

        recruit.get(0).setProject(newProject);
        Project theProject = projectRepository.save(newProject);

        JSONObject body = new JSONObject();
        body.appendField("projectId", theProject.getId());

        // when
        mockMvc.perform(post("/project/deadline")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(newProject.getTitle()))
                .andDo(print());

    }

    @Test
    @DisplayName("모집글 스크랩")
    void scrap() throws Exception {
        // given
        User user = new User("scrap", Platform.APPLE, "scrapTest");
        user = userRepository.save(user);

        List<Stack> skill = new ArrayList<>();
        skill.add(Stack.JAVA);
        skill.add(Stack.SPRINGBOOT);

        List<Part> recruit = new ArrayList<>();
        recruit.add(Part.builder()
                .recruitPart(Field.BACKEND)
                .recruitNum(3)
                .recruitSkill(skill)
                .requirement("아무거나")
                .build());


        Project newProject = Project.builder()
                .title("New project")
                .overview("This is new Project.")
                .dueDate(LocalDateTime.of(2050,11,1,0,0,0))
                .startDate(LocalDateTime.of(2023,11,1,0,0,0))
                .endDate(LocalDateTime.of(2023,11,1,0,0,0))
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .user(user)
                .stage("Before Start")
                .build();

        recruit.get(0).setProject(newProject);
        newProject = projectRepository.save(newProject);

        JSONObject body = new JSONObject();
        body.appendField("projectId", newProject.getId());

        String token = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();


        // when
        mockMvc.perform(post("/project/scrap")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.scrap").value("스크랩이 설정되었습니다."))
                .andDo(print());

    }


    @Test
    @DisplayName("지원한 프로젝트 목록 조회")
    void getApplyProjects() throws Exception {
        //given
        User user1 = new User("bridge1", Platform.APPLE, "1");

        Project project1 = Project.builder()
                .title("title1")
                .overview("overview1")
                .stage("stage1")
                .dueDate(LocalDateTime.of(2050,11,1,0,0,0))
                .build();

        projectRepository.save(project1);

        ApplyProject applyProject1 = new ApplyProject();
        applyProject1.setUserAndProject(user1, project1);

        user1.getApplyProjects().add(applyProject1);
        User saveUser1 = userRepository.save(user1);

        String token = Jwts.builder()
                .setSubject(String.valueOf(user1.getId()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        //expected
        mockMvc.perform(get("/projects/apply")
                    .header("Authorization", "Bearer " + token)
                    .param("userId", saveUser1.getId().toString())
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stage").value("결과 대기중"))
                .andExpect(jsonPath("$[0].title").value("title1"))
                .andExpect(jsonPath("$[0].overview").value("overview1"))
                .andExpect(jsonPath("$[0].dueDate").value(LocalDateTime.of(2050,11,1,0,0,0).toString()))
                .andDo(print());
    }

    @Test
    @DisplayName("프로젝트 지원하기")
    void applyProjects() throws Exception {
        //given
        User user1 = new User("bridge1", Platform.APPLE, "1");
        User user2 = new User("bridge2", Platform.APPLE, "2");
        user1.updateDeviceToken("deviceToken");
        user2.updateDeviceToken("deviceToken");
        User saveUser1 = userRepository.save(user1);
        User saveUser2 = userRepository.save(user2);

        Project project1 = Project.builder()
                .title("title1")
                .overview("overview1")
                .stage("stage1")
                .dueDate(LocalDateTime.of(2050,11,1,0,0,0))
                .user(user1)
                .build();

        Project saveProject = projectRepository.save(project1);

        String token = Jwts.builder()
                .setSubject(String.valueOf(saveUser2.getId()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        //expected
        mockMvc.perform(post("/projects/apply")
                        .header("Authorization", "Bearer " + token)
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
                .dueDate(LocalDateTime.of(2050,11,1,0,0,0))
                .build();

        Project saveProject = projectRepository.save(project1);

        ApplyProject applyProject1 = new ApplyProject();
        applyProject1.setUserAndProject(user1, project1);

        user1.getApplyProjects().add(applyProject1);
        User saveUser1 = userRepository.save(user1);

        String token = Jwts.builder()
                .setSubject(String.valueOf(saveUser1.getId()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        //expected
        mockMvc.perform(post("/projects/apply/cancel")
                        .header("Authorization", "Bearer " + token)
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
        User user1 = new User("bridge1", Platform.APPLE, "1");

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
                .dueDate(LocalDateTime.of(2050,11,1,0,0,0))
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
                .dueDate(LocalDateTime.of(2050,11,1,0,0,0))
                .user(user1)
                .build();

        Project saveProject = projectRepository.save(project1);

        ApplyProject applyProject1 = new ApplyProject();
        applyProject1.setUserAndProject(user1, project1);


        user1.getApplyProjects().add(applyProject1);
        User saveUser1 = userRepository.save(user1);

        String token = Jwts.builder()
                .setSubject(String.valueOf(saveUser1.getId()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        //expected
        mockMvc.perform(put("/projects/accept")
                        .header("Authorization", "Bearer " + token)
                        .param("projectId", saveProject.getId().toString())
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
                .dueDate(LocalDateTime.of(2050,11,1,0,0,0))
                .user(user1)
                .build();

        Project saveProject = projectRepository.save(project1);

        ApplyProject applyProject1 = new ApplyProject();
        applyProject1.setUserAndProject(user1, project1);


        user1.getApplyProjects().add(applyProject1);
        User saveUser1 = userRepository.save(user1);

        String token = Jwts.builder()
                .setSubject(String.valueOf(saveUser1.getId()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        //expected
        mockMvc.perform(put("/projects/reject")
                        .header("Authorization", "Bearer " + token)
                        .param("projectId", saveProject.getId().toString())
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