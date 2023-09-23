package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Platform;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.response.apple.AppleMemberResponse;
import com.Bridge.bridge.dto.response.apple.AppleResponse;
import com.Bridge.bridge.dto.response.OAuthTokenResponse;
import com.Bridge.bridge.repository.UserRepository;
import com.Bridge.bridge.dto.response.apple.AppleTokenResponse;
import com.Bridge.bridge.security.JwtTokenProvider;
import com.Bridge.bridge.security.apple.AppleToken;
import com.Bridge.bridge.security.apple.AppleUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppleUtils appleUtils;

    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;


    public OAuthTokenResponse appleOAuthLogin(AppleResponse response) throws Exception {
        // 1. 토큰을 통해 회원 정보 뺴기
        AppleMemberResponse appleUser = appleUtils.getAppleMember(response.getIdToken());
        return generateOAuthTokenResponse(appleUser.getEmail(), Platform.APPLE, appleUser.getSubject(), response.getName());
    }

    @Transactional
    public OAuthTokenResponse generateOAuthTokenResponse(String email, Platform platform, String platformId, String name) {
        return userRepository.findIdByPlatformAndPlatformId(platform, platformId)
                .map(userId ->  {
                    User findUser = userRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));
                    String accessToken = jwtTokenProvider.createAccessToken(findUser.getId());
                    String refreshToken = jwtTokenProvider.createRefreshToken();

                    return new OAuthTokenResponse(accessToken, refreshToken, email, true, platformId);
                })
                .orElseGet(() -> {
                    User saveUser = userRepository.save(new User(name, email, platform, platformId));
                    String accessToken = jwtTokenProvider.createAccessToken(saveUser.getId());
                    String refreshToken = jwtTokenProvider.createRefreshToken();

                    return new OAuthTokenResponse(accessToken, refreshToken, email, false, platformId);
                });
    }
}
