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
import com.Bridge.bridge.service.ProjectService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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

        ProjectRequestDto newProject = ProjectRequestDto.builder()
                .title("New project")
                .overview("This is new Project.")
                .dueDate("2023-09-07")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .userId(newUser.getId())
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
                .userId(saveUser.getId())
                .stage("Before Start")
                .build();

        Long projectId = projectService.createProject(newProject);

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
                .dueDate("2023-09-07")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
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
                .andExpect(status().is(202)) // 응답 status를 ok로 테스트
                .andDo(print());
    }

    @Test
    @DisplayName("모집글 상세보기")
    void detailProject() throws Exception {
        // given
        User user1 = new User("user", "user@gmail.com", Platform.APPLE, "Test");
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
        mockMvc.perform(get("/project")
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
        skill1.add("Java");
        skill1.add("Spring boot");

        List<String> skill2 = new ArrayList<>();
        skill2.add("Java");
        skill2.add("Spring boot");

        List<PartRequestDto> recruit1 = new ArrayList<>();
        recruit1.add(PartRequestDto.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill1)
                .requirement("backend")
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
                .dueDate("2023-09-07")
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
                .dueDate("2023-09-07")
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
        findSkills.add("Java");
        findSkills.add("Spring boot");

        FilterRequestDto filterRequestDto = FilterRequestDto.builder()
                .part("backend")
                .skills(findSkills)
                .build();

        String body = objectMapper.writeValueAsString(filterRequestDto);

        // when
        String expectByTitle = "$.[?(@.title == '%s')]";

        mockMvc.perform(post("/project/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(expectByTitle, "This is what i find").exists())
                .andDo(print());

    }

    @DisplayName("내가 작성한 모집글들 불러오기")
    @Test
    void findMyProjects() throws Exception {
        // given
        User user = new User("user", "user@gmail.com", Platform.APPLE, "Test");
        userRepository.save(user);

        List<String> skill1 = new ArrayList<>();
        skill1.add("Java");
        skill1.add("Spring boot");

        List<String> skill2 = new ArrayList<>();
        skill2.add("Java");
        skill2.add("Spring boot");

        List<PartRequestDto> recruit1 = new ArrayList<>();
        recruit1.add(PartRequestDto.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill1)
                .requirement("backend")
                .build());

        List<PartRequestDto> recruit2 = new ArrayList<>();
        recruit2.add(PartRequestDto.builder()
                .recruitPart("frontend")
                .recruitNum(1)
                .recruitSkill(skill2)
                .requirement("frontend")
                .build());

        ProjectRequestDto newProject1 = ProjectRequestDto.builder()
                .title("Myproject1")
                .overview("This is Myproject1")
                .dueDate("2023-09-07")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit1)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .userId(user.getId())
                .stage("Before Start")
                .build();

        ProjectRequestDto newProject2 = ProjectRequestDto.builder()
                .title("Myproject2")
                .overview("This is Myproject2")
                .dueDate("2023-09-07")
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

        // when
        String expectByTitle = "$.[?(@.title == '%s')]";

        mockMvc.perform(post("/projects/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user.getId())))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(expectByTitle, "Myproject2").exists())
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
        skill1.add("Java");
        skill1.add("Spring boot");

        List<String> skill2 = new ArrayList<>();
        skill2.add("Java");
        skill2.add("Spring boot");

        List<String> skill3 = new ArrayList<>();
        skill2.add("Python");
        skill2.add("Django");

        List<PartRequestDto> recruit1 = new ArrayList<>();
        recruit1.add(PartRequestDto.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill1)
                .requirement("backend")
                .build());

        List<PartRequestDto> recruit2 = new ArrayList<>();
        recruit2.add(PartRequestDto.builder()
                .recruitPart("frontend")
                .recruitNum(1)
                .recruitSkill(skill2)
                .requirement("frontend")
                .build());

        List<PartRequestDto> recruit3 = new ArrayList<>();
        recruit3.add(PartRequestDto.builder()
                .recruitPart("backtend")
                .recruitNum(5)
                .recruitSkill(skill3)
                .requirement("backend")
                .build());

        ProjectRequestDto newProject1 = ProjectRequestDto.builder()
                .title("Myproject1")
                .overview("This is Myproject1")
                .dueDate("2023-09-07")
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
                .dueDate("2023-09-07")
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
                .dueDate("2023-09-07")
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
        skill1.add("Java");
        skill1.add("Spring boot");

        List<String> skill2 = new ArrayList<>();
        skill2.add("Java");
        skill2.add("Spring boot");

        List<PartRequestDto> recruit1 = new ArrayList<>();
        recruit1.add(PartRequestDto.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill1)
                .requirement("backend")
                .build());

        List<PartRequestDto> recruit2 = new ArrayList<>();
        recruit2.add(PartRequestDto.builder()
                .recruitPart("frontend")
                .recruitNum(1)
                .recruitSkill(skill2)
                .requirement("frontend")
                .build());

        List<PartRequestDto> recruit3 = new ArrayList<>();
        recruit3.add(PartRequestDto.builder()
                .recruitPart("backend")
                .recruitNum(1)
                .recruitSkill(skill2)
                .requirement("backend")
                .build());

        ProjectRequestDto newProject1 = ProjectRequestDto.builder()
                .title("This is backend Project.")
                .overview("This is backend Project.")
                .dueDate("2023-09-07")
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
                .dueDate("2023-09-07")
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
                .dueDate("2023-09-07")
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
                .dueDate("20240101235959")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
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
                        .param("projectId", String.valueOf(projectId)))
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

        Long projectId = newProject.getId();
        Long userId = user.getId();

        // when
        mockMvc.perform(post("/project/scrap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("projectId", String.valueOf(projectId))
                        .content(objectMapper.writeValueAsString(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.scrap").value("스크랩이 설정되었습니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("최근 검색어 조회")
    void resentSearch() throws Exception {
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
        mockMvc.perform(get("/searchWords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(user.getId())))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].searchWord").value("검색어1"))
                .andDo(print());

    }

    @Test
    @DisplayName("최근 검색어 삭제")
    void deleteSearchWord() throws Exception {
        // given
        User user = new User("searchWord", "searchWord@gmail.com", Platform.APPLE, "searchWordTest");
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
        mockMvc.perform(delete("/searchWords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(user.getId()))
                        .content(objectMapper.writeValueAsString(newSearch1.getId())))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].searchWord").value("검색어2"))
                .andExpect(jsonPath("$[1].searchWord").value("검색어3"))
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
                .dueDate("23-10-10")
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
                .andExpect(jsonPath("$[0].dueDate").value("23-10-10"))
                .andDo(print());
    }

    @Test
    @DisplayName("프로젝트 지원하기")
    void applyProjects() throws Exception {
        //given
        User user1 = new User("bridge1", "bridge1@apple.com", Platform.APPLE, "1");
        userRepository.save(user1);

        Project project1 = Project.builder()
                .title("title1")
                .overview("overview1")
                .stage("stage1")
                .dueDate("23-10-10")
                .build();

        Project saveProject = projectRepository.save(project1);
        User saveUser1 = userRepository.save(user1);

        //expected
        mockMvc.perform(post("/projects/apply")
                        .param("userId", saveUser1.getId().toString())
                        .param("projectId", saveProject.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("프로젝트 지원 취소하기")
    void cancelApply() throws Exception {
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

        //expected
        mockMvc.perform(get("/projects/apply/users")
                        .param("projectId", saveProject.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(user1.getId()))
                .andExpect(jsonPath("$[0].name").value("bridge1"))
                .andExpect(jsonPath("$[0].fields[0]").value("Backend"))
                .andExpect(jsonPath("$[0].career").value("career1"))
                .andDo(print());
    }

    @Test
    @DisplayName("프로젝트 수락하기")
    void acceptApply() throws Exception {
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
    @DisplayName("프로젝트 수락하기")
    void rejectApply() throws Exception {
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

        //expected
        mockMvc.perform(get("/projects/top")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("제목"+day))
                .andDo(print());
    }
}