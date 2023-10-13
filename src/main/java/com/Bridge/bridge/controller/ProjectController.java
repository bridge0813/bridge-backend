package com.Bridge.bridge.controller;

import com.Bridge.bridge.dto.request.FilterRequestDto;
import com.Bridge.bridge.dto.response.*;
import com.Bridge.bridge.dto.request.ProjectRequestDto;
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
    @PostMapping("/projects/searchWord")
    public List<ProjectListResponseDto> searchProject(@RequestParam Long userId ,@RequestBody String searchWord){
        return projectService.findByTitleAndContent(userId, searchWord);
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


    // 내가 작성한 프로젝트 모집글 불러오기
    @PostMapping("/projects/")
    public List<MyProjectResponseDto> findMyProjects(@RequestBody Long userId){
        return projectService.findMyProjects(userId);
    }

    // 모든 프로젝트 모집글 불러오기
    @GetMapping("/projects/all")
    public List<ProjectListResponseDto> allProjects(){
        return projectService.allProjects();
    }

    // 내 분야 프로젝트 모집글 불러오기
    @PostMapping("/projects/mypart")
    public List<ProjectListResponseDto> findMyPartProjects(@RequestBody String part){
        return projectService.findMyPartProjects(part);
    }

    // 모집글 마감하기
    @PostMapping("/project/deadline")
    public ProjectResponseDto closeProject(@RequestParam Long projectId, @RequestBody Long userId){
        return projectService.closeProject(projectId, userId);
    }

    // 모집글 스크랩하기
    @PostMapping("/project/scrap")
    public BookmarkResponseDto scrap(@RequestParam Long projectId, @RequestBody Long userId){

        return projectService.scrap(projectId, userId);

    }
  
    /**
     * 지원한 프로젝트 목록 조회
     */
    @GetMapping("/projects/apply")
    public ResponseEntity<?> getApplyProjects(@RequestParam("userId") Long userId ) {
        List<ApplyProjectResponse> applyProjects = projectService.getApplyProjects(userId);
        return ResponseEntity.ok(applyProjects);
    }

    // 최근 검색어 불러오기 기능
    @GetMapping("/searchWords")
    public List<SearchWordResponseDto> resentSearchWord(@RequestParam("userId") Long userId){
        return projectService.resentSearchWord(userId);
    }

    // 최근 검색어 불러오기 기능
    @DeleteMapping("/searchWords")
    public List<SearchWordResponseDto> deleteSearchWord(@RequestParam("userId") Long userId, @RequestBody Long searchWordId){
        return projectService.deleteSearchWord(userId, searchWordId);
    }

    /**
     * 프로젝트 지원하기
     */
    @PostMapping("/projects/apply")
    public ResponseEntity<?> applyProjects(@RequestParam("userId") Long userId, @RequestParam("projectId") Long projectId) {
        boolean result = projectService.apply(userId, projectId);
        return ResponseEntity.ok(result);
    }

    /**
     * 프로젝트 지원 취소하기
     */
    @PostMapping("/projects/apply/cancel")
    public ResponseEntity<?> cancelApply(@RequestParam("userId") Long userId, @RequestParam("projectId") Long projectId) {
        boolean result = projectService.cancelApply(userId, projectId);
        return ResponseEntity.ok(result);
    }

    /**
     * 프로젝트 지원자 목록
     */
    @GetMapping("/projects/apply/users")
    public ResponseEntity<?> applyUsers(@RequestParam("projectId") Long projectId) {
        List<ApplyUserResponse> applyUsers = projectService.getApplyUsers(projectId);
        return ResponseEntity.ok(applyUsers);
    }

    // 인기글 조회 기능
    @GetMapping("/projects/top")
    public List<TopProjectResponseDto> topProjects(){
        return projectService.topProjects();
    }
}
