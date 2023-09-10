package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.ProjectListDto;
import com.Bridge.bridge.dto.ReqPartDto;
import com.Bridge.bridge.dto.ReqProjectDto;
import com.Bridge.bridge.repository.ProjectRepository;
import com.Bridge.bridge.domain.Project;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectServiceTest {

    @Autowired
    ProjectService projectService;
    @Autowired
    ProjectRepository projectRepository;

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
        List<String> skill = new ArrayList<>();
        skill.add("Java");
        skill.add("Spring boot");

        List<ReqPartDto> recruit = new ArrayList<>();
        recruit.add(ReqPartDto.builder()
                .recruitPart("backend")
                .recruitNum(3)
                .recruitSkill(skill)
                .requirement("아무거나")
                .build());

        ReqProjectDto newProject = ReqProjectDto.builder()
                .title("New project")
                .overview("This is new Project.")
                .dueDate("2023-09-07")
                .startDate("2023-09-11")
                .endDate("2023-09-30")
                .recruit(recruit)
                .tagLimit(new ArrayList<>())
                .meetingWay("Offline")
                .userEmail("newUser@gmail.com")
                .stage("Before Start")
                .build();

        // when
        ResponseEntity result = projectService.createProject(newProject);

        // then

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}