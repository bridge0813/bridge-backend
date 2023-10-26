package com.Bridge.bridge.controller;

import com.Bridge.bridge.dto.request.FilterRequestDto;
import com.Bridge.bridge.dto.request.NotificationRequestDto;
import com.Bridge.bridge.dto.request.ProjectUpdateRequestDto;
import com.Bridge.bridge.dto.response.*;
import com.Bridge.bridge.dto.request.ProjectRequestDto;
import com.Bridge.bridge.service.AlarmService;
import com.Bridge.bridge.service.ProjectService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final AlarmService alarmService;


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
    public ResponseEntity deleteProject(@RequestParam Long projectId){
        Boolean result = projectService.deleteProject(projectId);

        if (result.equals(true)){
            return new ResponseEntity(HttpStatus.ACCEPTED);
        }
        else{
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    // 프로젝트 모집글 수정
    @PutMapping("/project")
    public ProjectResponseDto updateProject(@RequestParam Long projectId, @RequestBody ProjectUpdateRequestDto projectUpdateRequestDto){
        return projectService.updateProject(projectId, projectUpdateRequestDto);
    }

    // 프로젝트 모집글 상세보기
    @GetMapping("/project")
    public ProjectResponseDto detailProject(HttpServletRequest request, @RequestParam Long projectId){
        return projectService.getProject(projectId, request);
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
    public ProjectResponseDto closeProject(@RequestParam Long projectId){
        return projectService.closeProject(projectId);
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
    public ResponseEntity<?> applyProjects(@RequestParam("userId") Long userId, @RequestParam("projectId") Long projectId) throws FirebaseMessagingException {
        boolean result = projectService.apply(userId, projectId);

        // 모집자에게 지원자 발생 알림 보내기
        alarmService.getApplyAlarm(projectId);

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

    /**
     * 프로젝트 수락하기
     */
    @PutMapping("/projects/accept")
    public ResponseEntity<?> acceptApply(@RequestParam("projectId") Long projectId,
                                         @RequestParam("userId") Long userId) throws FirebaseMessagingException {
        projectService.acceptApply(projectId, userId);

        // 지원 결과 알림 보내기
        alarmService.getApplyResultAlarm(userId);

        // 알림보내기
        NotificationRequestDto notificationRequestDto = NotificationRequestDto.builder()
                .userID(userId)
                .title("지원 결과 도착")
                .body("내가 지원한 프로젝트의 결과가 나왔어요. 관리 페이지에서 확인해보세요.")
                .build();

        alarmService.sendNotification(notificationRequestDto);

        return ResponseEntity.ok(true);
    }

    /**
     * 프로젝트 거절하기
     */
    @PutMapping("/projects/reject")
    public ResponseEntity<?> rejectApply(@RequestParam("projectId") Long projectId,
                                         @RequestParam("userId") Long userId) throws FirebaseMessagingException {
        projectService.rejectApply(projectId, userId);

        // 지원 결과 알림 보내기
        alarmService.getApplyResultAlarm(userId);

        // 알림보내기
        NotificationRequestDto notificationRequestDto = NotificationRequestDto.builder()
                .userID(userId)
                .title("지원 결과 도착")
                .body("내가 지원한 프로젝트의 결과가 나왔어요. 관리 페이지에서 확인해보세요.")
                .build();

        alarmService.sendNotification(notificationRequestDto);

        return ResponseEntity.ok(true);
    }

    // 인기글 조회 기능
    @GetMapping("/projects/top")
    public List<TopProjectResponseDto> topProjects(){
        return projectService.topProjects();

    }

    // 마감 임박 모집글 조회 기능
    @GetMapping("/projects/imminent")
    public List<imminentProjectResponse> imminentProjects(){
        return projectService.getdeadlineImminentProejcts();

    }
}
