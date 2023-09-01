package com.Bridge.bridge.controller;

import com.Bridge.bridge.dto.ProjectListDto;
import com.Bridge.bridge.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/searchProject")
    public List<ProjectListDto> searchProject(@RequestBody String searchWord){
        return projectService.findByTitleAndContent(searchWord);
    }

}
