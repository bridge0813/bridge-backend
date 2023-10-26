package com.Bridge.bridge.controller;

import com.Bridge.bridge.dto.request.ProfileUpdateRequest;
import com.Bridge.bridge.dto.request.UserFieldRequest;
import com.Bridge.bridge.dto.request.UserProfileRequest;
import com.Bridge.bridge.dto.response.ErrorResponse;
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

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


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
                                           @RequestPart(value = "refFile", required = false)MultipartFile refFile) {
        userService.saveProfile(userId, userProfileRequest, photo, refFile);

        // TODO : 리다이렉트?
        return ResponseEntity.ok(true);
    }

    /**
     * 유저 개인 프로필 확인
     */
    @GetMapping("/users/profile")
    public ResponseEntity<?> showProfile(@RequestParam("userId") Long userId) {
        UserProfileResponse profile = userService.getProfile(userId);

        return ResponseEntity.ok(profile);
    }

    /**
     * 프로필 수정
     */
    @PutMapping("/users/profile")
    public ResponseEntity<?> updateProfile(@RequestParam("userId") Long userId, @RequestPart("request") ProfileUpdateRequest request,
                                           @RequestPart(value = "photo", required = false) MultipartFile photo,
                                           @RequestPart(value = "refFile", required = false) MultipartFile refFile) {
        userService.updateProfile(userId, request, photo, refFile);
        return ResponseEntity.ok(true);
    }
}
