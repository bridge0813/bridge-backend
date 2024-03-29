package com.Bridge.bridge.controller;

import com.Bridge.bridge.dto.request.FieldUpdateRequest;
import com.Bridge.bridge.dto.request.ProfileUpdateRequest;
import com.Bridge.bridge.dto.request.UserFieldRequest;
import com.Bridge.bridge.dto.request.UserProfileRequest;
import com.Bridge.bridge.dto.response.BookmarkListResponse;
import com.Bridge.bridge.dto.response.ErrorResponse;
import com.Bridge.bridge.dto.response.MyPageResponse;
import com.Bridge.bridge.dto.response.UserProfileResponse;
import com.Bridge.bridge.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Tag(name = "User", description = "유저")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원 가입시 관심 분야 등록
     */
    @Operation(summary = "로그인 후 회원가입", description = "소셜 로그인 후에 관심분야를 설정하는 회원가입")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공 (OK)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "인증 실패 (Unauthorized)", content =
                @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity saveField(@RequestBody UserFieldRequest userFieldRequest) {
        userService.saveField(userFieldRequest);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 프로필 등록
     */
    @PostMapping("/users/profile")
    public ResponseEntity<?> createProfile(@RequestParam("userId") Long userId,
                                           @RequestPart("profile") UserProfileRequest userProfileRequest,
                                           @RequestPart(value = "photo", required = false)MultipartFile photo,
                                           @RequestPart(value = "refFiles", required = false)List<MultipartFile> refFiles) {
        userService.saveProfile(userId, userProfileRequest, photo, refFiles);

        return ResponseEntity.ok(true);
    }

    /**
     * 유저 개인 프로필 확인
     */
    @GetMapping("/users/profile/one")
    public ResponseEntity<?> showProfile(@RequestParam("userId") Long userId) {
        UserProfileResponse profile = userService.getProfile(userId);

        return ResponseEntity.ok(profile);
    }

    /**
     * 유저 관심분야 수정
     */
    @PutMapping("/users/field")
    public ResponseEntity<?> updateFields(@RequestBody FieldUpdateRequest request) {
        boolean res = userService.updateField(request);
        return ResponseEntity.ok(res);
    }

    /**
     * 프로필 수정
     */
    @PutMapping("/users/profile")
    public ResponseEntity<?> updateProfile(@RequestParam("userId") Long userId, @RequestPart("request") ProfileUpdateRequest request,
                                           @RequestPart(value = "photo", required = false) MultipartFile photo,
                                           @RequestPart(value = "refFiles", required = false) List<MultipartFile> refFiles) {
        userService.updateProfile(userId, request, photo, refFiles);
        return ResponseEntity.ok(true);
    }

    /**
     * 북마크 프로젝트 목록 조회
     */
    @GetMapping("/users/bookmark")
    public ResponseEntity<?> showBookmarkProjects(@RequestParam("userId") Long userId) {
        List<BookmarkListResponse> bookmarkProjects = userService.getBookmarkProjects(userId);
        return ResponseEntity.ok(bookmarkProjects);
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam("userId") Long userId) {
        boolean result = userService.logout(userId);
        return ResponseEntity.ok(result);
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId) {
        boolean result = userService.deleteUser(userId);
        return ResponseEntity.ok(result);
    }

    /**
     * 마이페이지
     */
    @GetMapping("/users/mypage")
    public ResponseEntity<?> getMyPage(@RequestParam("userId") Long userId) {
        MyPageResponse myPage = userService.getMyPage(userId);
        return ResponseEntity.ok(myPage);
    }
}
