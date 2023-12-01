package com.Bridge.bridge.controller;

import com.Bridge.bridge.dto.request.*;
import com.Bridge.bridge.dto.response.*;
import com.Bridge.bridge.service.AlarmService;
import com.Bridge.bridge.service.ProjectService;
import com.google.firebase.messaging.FirebaseMessagingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final AlarmService alarmService;


    // 프로젝트 모집글 작성
    @Operation(summary = "프로젝트 모집글 작성 기능", description = "프로젝트 모집글을 작성할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모집글 작성 완료"),
            @ApiResponse(responseCode = "400", description = "모집글 작성 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (Unauthorized)"),
            @ApiResponse(responseCode = "404", description = "유저 찾기 실패")
    })
    @PostMapping("/project")
    public ResponseEntity<?> createProject(@RequestBody ProjectRequestDto projectRequestDto){
        Map<String, Long> result = new HashMap<>();
        Long projectId = projectService.createProject(projectRequestDto);
        result.put("projectId", projectId);
        return ResponseEntity.ok(result);
    }

    // 검색어 기준으로 프로젝트 모집글 조회
    @PostMapping("/projects/searchWord")
    @Operation(summary = "검색어로 프로젝트 모집글 조회 기능", description = "검색어를 입력하면 검색어가 포함된 제목이나 내용을 가진 모집글을 조회할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모집글 조회 완료"),
            @ApiResponse(responseCode = "400", description = "모집글 조회 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (Unauthorized)"),
            @ApiResponse(responseCode = "404", description = "유저 찾기 실패")
    })
    public List<ProjectListResponseDto> searchProject(@RequestParam Long userId ,@RequestBody String searchWord){
        return projectService.findByTitleAndContent(userId, searchWord);
    }

    // 프로젝트 모집글 삭제
    @DeleteMapping("/project")
    @Operation(summary = "모집글 삭제 기능", description = "모집글을 삭제할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모집글 삭제 완료"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (Unauthorized)"),
            @ApiResponse(responseCode = "403", description = "모집글 삭제 실패"),
            @ApiResponse(responseCode = "404", description = "유저 찾기 실패 OR 모집글 찾기 실패")
    })
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
    @Operation(summary = "모집글 수정 기능", description = "모집글을 수정할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모집글 수정 완료"),
            @ApiResponse(responseCode = "400", description = "모집글 수정 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (Unauthorized)"),
            @ApiResponse(responseCode = "404", description = "유저 찾기 실패 OR 모집글 찾기 실패")
    })
    public ProjectResponseDto updateProject(@RequestParam Long projectId, @RequestBody ProjectUpdateRequestDto projectUpdateRequestDto){
        return projectService.updateProject(projectId, projectUpdateRequestDto);
    }

    // 프로젝트 모집글 상세보기
    @GetMapping("/project")
    @Operation(summary = "모집글 상세보기 기능", description = "모집글의 구체적인 사항들을 조회할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모집글 상세보기 조회 완료"),
            @ApiResponse(responseCode = "400", description = "모집글 상세보기 조회 실패"),
            @ApiResponse(responseCode = "404", description = "유저 찾기 실패 OR 모집글 찾기 실패")
    })
    public ProjectResponseDto detailProject(HttpServletRequest request, @RequestParam Long projectId){
        return projectService.getProject(projectId, request);
    }

    // 프로젝트 모집글 필터링 조회
    @PostMapping("/projects/category")
    @Operation(summary = "필터링을 이용한 모집글 조회 기능", description = "스택이나 관심분야를 기준으로 필터링하여 모집글을 조회할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모집글 필터링 조회 완료"),
            @ApiResponse(responseCode = "400", description = "모집글 필터링 조회 실패")
    })
    public List<ProjectListResponseDto> filterProjects(HttpServletRequest request, @RequestBody FilterRequestDto filterRequestDto){
        return projectService.filterProjectList(request, filterRequestDto);
    }


    // 내가 작성한 프로젝트 모집글 불러오기
    @GetMapping("/projects")
    @Operation(summary = "내가 작성한 프로젝트 모집글 조회 기능", description = "내가 작성한 프로젝트 모집글들을 조회할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내가 작성한 프로젝트 모집글 조회 완료"),
            @ApiResponse(responseCode = "400", description = "내가 작성한 프로젝트 모집글 조회 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (Unauthorized)"),
            @ApiResponse(responseCode = "404", description = "유저 찾기 실패 OR 모집글 찾기 실패")
    })
    public List<MyProjectResponseDto> findMyProjects(@RequestParam Long userId){
        return projectService.findMyProjects(userId);
    }

    // 모든 프로젝트 모집글 불러오기
    @GetMapping("/projects/all")
    @Operation(summary = "모든 프로젝트 모집글 조회 기능", description = "전체 모집글을 조회할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 모집글 조회 완료"),
            @ApiResponse(responseCode = "400", description = "전체 모집글 조회 실패"),
            @ApiResponse(responseCode = "404", description = "모집글이 존재하지 않는다.")
    })
    public List<ProjectListResponseDto> allProjects(HttpServletRequest request){
        return projectService.allProjects(request);
    }

    // 내 분야 프로젝트 모집글 불러오기
    @PostMapping("/projects/mypart")
    @Operation(summary = "내 분야 프로젝트 모집글 조회 기능", description = "내 분야 프로젝트 모집글들을 조회할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내가 작성한 프로젝트 모집글 조회 완료"),
            @ApiResponse(responseCode = "400", description = "내가 작성한 프로젝트 모집글 조회 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (Unauthorized)"),
            @ApiResponse(responseCode = "404", description = "유저 찾기 실패 OR 모집글 찾기 실패")
    })
    public List<ProjectListResponseDto> findMyPartProjects(HttpServletRequest request, @RequestBody myPartProjectRequest myPartProjectRequest){
        return projectService.findMyPartProjects(request, myPartProjectRequest.getPart());
    }

    // 모집글 마감하기
    @PostMapping("/project/deadline")
    @Operation(summary = "모집글 마감 기능", description = "내가 작성한 프로젝트 모집글을 마감할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모집글 마감 완료"),
            @ApiResponse(responseCode = "400", description = "모집글 마감 실패 - 이미 마감이 된 모집글"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (Unauthorized)"),
            @ApiResponse(responseCode = "404", description = "모집글 찾기 실패")
    })
    public ProjectResponseDto closeProject(@RequestBody Long projectId){
        return projectService.closeProject(projectId);
    }

    // 모집글 스크랩하기
    @PostMapping("/project/scrap")
    @Operation(summary = "모집글 스크랩 기능", description = "모집글을 스크랩할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모집글 스크랩 설정 OR 해제 완료"),
            @ApiResponse(responseCode = "400", description = "모집글 스크랩 설정 OR 해제 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (Unauthorized)"),
            @ApiResponse(responseCode = "404", description = "유저 찾기 실패 OR 모집글 찾기 실패")
    })
    public BookmarkResponseDto scrap(@RequestParam Long userId, @RequestBody Long projectId){

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

    /**
     * 프로젝트 지원하기
     */
    @PostMapping("/projects/apply")
    public ResponseEntity<?> applyProjects(@RequestParam("userId") Long userId, @RequestParam("projectId") Long projectId) throws FirebaseMessagingException{
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
    @GetMapping("/projects/top") @Operation(summary = "인기글 조회 기능", description = "인기글을 조회할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인기글 조회 완료"),
            @ApiResponse(responseCode = "400", description = "인기글 조회 실패"),
            @ApiResponse(responseCode = "404", description = "모집글 찾기 실패")
    })
    public List<TopProjectResponseDto> topProjects(){
        return projectService.topProjects();

    }

    // 마감 임박 모집글 조회 기능
    @GetMapping("/projects/imminent")
    @Operation(summary = "마감 임박 모집글 조회 기능", description = "마감 임박 모집글을 조회할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "마감 임박 모집글 조회 완료"),
            @ApiResponse(responseCode = "400", description = "마감 임박 모집글 조회 실패"),
            @ApiResponse(responseCode = "404", description = "모집글 찾기 실패")
    })
    public List<imminentProjectResponse> imminentProjects(){
        return projectService.getdeadlineImminentProejcts();
    }
}
