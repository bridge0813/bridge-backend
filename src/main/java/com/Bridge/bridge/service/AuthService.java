package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.response.AppleMemberResponse;
import com.Bridge.bridge.dto.response.AppleResponse;
import com.Bridge.bridge.dto.response.OAuthTokenResponse;
import com.Bridge.bridge.repository.UserRepository;
import com.Bridge.bridge.security.AppleTokenResponse;
import com.Bridge.bridge.security.apple.AppleToken;
import com.Bridge.bridge.security.apple.AppleUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppleUtils appleUtils;

    private final UserRepository userRepository;

    private final AppleToken appleToken;

    public OAuthTokenResponse appleOAuthLogin(AppleResponse response) throws Exception {
        // 1. 토큰을 통해 회원 정보 뺴기
        AppleMemberResponse appleUser = appleUtils.getAppleMember(response.getIdToken());
        return generateOAuthTokenResponse(appleUser.getEmail(), appleUser.getSubject(), response.getCode());
    }

    public OAuthTokenResponse generateOAuthTokenResponse(String email, String platformId, String code) throws Exception {
        AppleTokenResponse accessToken = appleToken.getAccessToken(code);
        if (userRepository.findByPlatformId(platformId).isPresent()) {
            return new OAuthTokenResponse(accessToken.getAccessToken(), email, true, platformId);
        }
        User newUser = new User(email, platformId);

        userRepository.save(newUser);
        return new OAuthTokenResponse(accessToken.getAccessToken(), email, false, platformId);
    }
}
