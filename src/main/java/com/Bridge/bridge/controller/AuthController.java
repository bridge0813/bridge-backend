package com.Bridge.bridge.controller;

import com.Bridge.bridge.dto.response.AppleResponse;
import com.Bridge.bridge.dto.response.OAuthTokenResponse;
import com.Bridge.bridge.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class AuthController {

    private AuthService authService;

    /**
     * 애플 로그인 후 애플서버로 부터 받아오는 데이터
     * @param appleResponse
     */
    @PostMapping("/login/apple")
    public ResponseEntity<OAuthTokenResponse> appleLogin(@RequestBody AppleResponse appleResponse) throws Exception {
        OAuthTokenResponse response = authService.appleOAuthLogin(appleResponse);
        return ResponseEntity.ok(response);
    }
}
