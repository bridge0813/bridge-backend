package com.Bridge.bridge.controller;

import com.Bridge.bridge.dto.request.FilterRequestDto;
import com.Bridge.bridge.dto.response.ApplyProjectResponse;
import com.Bridge.bridge.dto.response.ProjectListResponseDto;
import com.Bridge.bridge.dto.request.ProjectRequestDto;
import com.Bridge.bridge.dto.response.ProjectResponseDto;
import com.Bridge.bridge.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;


    // 프로젝트 모집글 작성
    @PostMapping("/project")
    public Long createProject(@RequestBody ProjectRequestDto projectRequestDto){
        return projectService.createProject(projectRequestDto);
    }

    // 검색어 기준으로 프로젝트 모집글 조회
    @GetMapping("/project/list")
    public List<ProjectListResponseDto> searchProject(@RequestParam String searchWord){
        return projectService.findByTitleAndContent(searchWord);
    }

    // 프로젝트 모집글 삭제
    @DeleteMapping("/project")
    public ResponseEntity deleteProject(@RequestParam Long projectId, @RequestBody Long userId){
        Boolean result = projectService.deleteProject(projectId, userId);

        if (result.equals(true)){
            return new ResponseEntity(HttpStatus.ACCEPTED);
        }
        else{
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    // 프로젝트 모집글 수정
    @PutMapping("/project")
    public ProjectResponseDto updateProject(@RequestParam Long projectId, @RequestBody ProjectRequestDto projectRequestDto){
        return projectService.updateProject(projectId, projectRequestDto);
    }

    // 프로젝트 모집글 상세보기
    @GetMapping("/project")
    public ProjectResponseDto detailProject(@RequestParam Long projectId){
        return projectService.getProject(projectId);
    }

    // 프로젝트 모집글 필터링 조회
    @PostMapping("/project/category")
    public List<ProjectListResponseDto> filterProjects(@RequestBody FilterRequestDto filterRequestDto){
        return projectService.filterProjectList(filterRequestDto);
    }

    /**
     * 지원한 프로젝트 목록 조회
     */
    @GetMapping("/projects/apply")
    public ResponseEntity<?> getApplyProjects(@RequestParam("userId") Long userId ) {
        List<ApplyProjectResponse> applyProjects = projectService.getApplyProjects(userId);
        return ResponseEntity.ok(applyProjects);
    }

    /**
     * 프로젝트 지원하기
     */
    @PostMapping("/projects/apply")
    public ResponseEntity<?> applyProjects(@RequestParam("userId") Long userId, @RequestParam("projectId") Long projectId) {
        boolean result = projectService.apply(userId, projectId);
        return ResponseEntity.ok(result);
    }


}
