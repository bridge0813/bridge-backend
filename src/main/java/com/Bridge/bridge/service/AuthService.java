package com.Bridge.bridge.service;

import com.Bridge.bridge.dto.response.AppleMemberResponse;
import com.Bridge.bridge.dto.response.AppleResponse;
import com.Bridge.bridge.dto.response.OAuthTokenResponse;
import com.Bridge.bridge.security.apple.AppleUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppleUtils appleUtils;

    public OAuthTokenResponse appleOAuthLogin(AppleResponse response) throws Exception {
        // 1. 토큰을 통해 회원 정보 뺴기
        AppleMemberResponse appleMember = appleUtils.getAppleMember(response.getIdToken());
        return null;
    }
}
