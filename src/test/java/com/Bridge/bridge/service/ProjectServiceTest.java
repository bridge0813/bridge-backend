package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Part;
import com.Bridge.bridge.domain.Project;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.ProjectListDto;
import com.Bridge.bridge.dto.PartRequestDto;
import com.Bridge.bridge.dto.ProjectRequestDto;
import com.Bridge.bridge.repository.ProjectRepository;
import com.Bridge.bridge.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectServiceTest {

    @Autowired
    ProjectService projectService;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    UserRepository userRepository;

    @DisplayName("모집글 검색 기능 test")
    @Test
    public void findProjects() {
        // given

        // When
        List<ProjectListDto> result = projectService.findByTitleAndContent("어플");

        // Then
        assertEquals(result.size(), 4);

    }

    @DisplayName("모집글 생성 기능 test")
    @Test
    void createProject() {
        // given
        User user = new User("test1@gmaill.com", "apple");
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
                .userEmail(user.getEmail())
                .stage("Before Start")
                .build();

        // when
        HttpStatus result = projectService.createProject(newProject);

        // then
        Assertions.assertThat(result).isEqualTo(HttpStatus.OK);

        Project project = projectRepository.findByUser_Id(user.getId()).get();
        projectRepository.delete(project);
        userRepository.delete(user);
    }

    @DisplayName("프로젝트 삭제 기능 - 삭제하려는 유저가 DB에 있을 때(올바른 접근)")
    @Test
    void deleteProject() {
        // given
        User user = new User("test2@gmaill.com", "apple");
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
                .userEmail(user.getEmail())
                .stage("Before Start")
                .build();

        projectService.createProject(newProject);

        Long userId = user.getId();
        Long projectId = projectRepository.findByUser_Id(userId).get().getId();


        // when
        HttpStatus result = projectService.deleteProject(projectId, userId);

        // then
        Assertions.assertThat(result).isEqualTo(HttpStatus.ACCEPTED);
        userRepository.delete(user);

    }

    @DisplayName("프로젝트 삭제 기능 - 삭제하려는 유저가 DB에 없을 때(올바르지 못한 접근)")
    @Test
    void deleteProject_Wrong() {
        // given
        User user1 = new User("ImUser@gmail.com", "apple");
        userRepository.save(user1);

        User user2 = new User("NotUser@gmail.com", "google");
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
                .userEmail(user1.getEmail())
                .stage("Before Start")
                .build();

        projectService.createProject(newProject);

        Long userId = user1.getId();
        Long projectId = projectRepository.findByUser_Id(userId).get().getId();

        // when
        HttpStatus result = projectService.deleteProject(projectId, user2.getId());

        // then
        Assertions.assertThat(result).isEqualTo(HttpStatus.BAD_REQUEST);

        Project project = projectRepository.findByUser_Id(user1.getId()).get();
        projectRepository.delete(project);

        userRepository.delete(user1);
        userRepository.delete(user2);
    }

}