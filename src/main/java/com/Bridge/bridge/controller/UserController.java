package com.Bridge.bridge.controller;

import com.Bridge.bridge.dto.request.UserRegisterRequest;
import com.Bridge.bridge.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원 가입시 관심 분야 등록
     */
    @PostMapping("/users/signup")
    public ResponseEntity saveField(@RequestBody UserRegisterRequest request) {
        userService.signUpInfo(request);
        return new ResponseEntity(HttpStatus.OK);
    }
}
