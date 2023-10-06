package com.Bridge.bridge.controller;

import com.Bridge.bridge.domain.Part;
import com.Bridge.bridge.domain.Platform;
import com.Bridge.bridge.domain.Project;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.FilterRequestDto;
import com.Bridge.bridge.dto.request.PartRequestDto;
import com.Bridge.bridge.dto.request.ProjectRequestDto;
import com.Bridge.bridge.repository.ProjectRepository;
import com.Bridge.bridge.repository.UserRepository;
import com.Bridge.bridge.service.ProjectService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
    private ProjectService projectService;

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

        projectService.createProject(newProject);

        Long userId = user.getId();
        Long projectId = projectRepository.findByUser_Id(userId).get().getId();


        // when
        mockMvc.perform(delete("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("projectId", String.valueOf(projectId))
                        .content(objectMapper.writeValueAsString(userId)))
                .andExpect(status().is(202)) // 응답 status를 ok로 테스트
                .andDo(print());

    }

    @Test
    @DisplayName("모집글 수정")
    void updateProject() throws Exception {
        // given
        User user = new User("user", "user@gmail.com", Platform.APPLE, "Test");
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



        Long userId = user.getId();
        Long projectId = projectRepository.findByUser_Id(userId).get().getId();


        // when
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("userId", userId.toString());
        data.add("ProjectRequestDto", updateProject.toString());

        mockMvc.perform(put("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("projectId", String.valueOf(projectId))
                        .content(objectMapper.writeValueAsString(data)))
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
        Long userId = user1.getId();

        // when
        mockMvc.perform(post("/project/deadline")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("projectId", String.valueOf(projectId))
                        .content(objectMapper.writeValueAsString(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(newProject.getTitle()))
                .andDo(print());

    }




}