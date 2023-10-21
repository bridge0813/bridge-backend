package com.Bridge.bridge.controller;

import com.Bridge.bridge.dto.request.UserFieldRequest;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * 유저 개인 프로필 확인
     */
    @GetMapping("/users/profile")
    public ResponseEntity<?> showProfile(@RequestParam("userId") Long userId) {
        UserProfileResponse profile = userService.getProfile(userId);

        return ResponseEntity.ok(profile);
    }
}
