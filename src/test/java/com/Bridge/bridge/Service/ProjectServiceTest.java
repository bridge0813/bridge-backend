package com.Bridge.bridge.Service;

import com.Bridge.bridge.Repository.ProjectRepository;
import com.Bridge.bridge.domain.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        List<Project> result = projectService.findByTitleAndContent("어플");

        // Then
        assertEquals(result.size(), 4);

    }
}