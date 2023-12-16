package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Platform;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.response.apple.AppleMemberResponse;
import com.Bridge.bridge.dto.response.apple.AppleResponse;
import com.Bridge.bridge.dto.response.OAuthTokenResponse;
import com.Bridge.bridge.exception.notfound.NotFoundUserException;
import com.Bridge.bridge.repository.UserRepository;
import com.Bridge.bridge.security.JwtTokenProvider;
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

    private final AlarmService alarmService;


    public OAuthTokenResponse appleOAuthLogin(AppleResponse response) throws Exception {
        // 1. 토큰을 통해 회원 정보 뺴기
        AppleMemberResponse appleUser = appleUtils.getAppleMember(response.getIdToken());
        return generateOAuthTokenResponse(Platform.APPLE, appleUser.getSubject(), response.getName(), response.getDeviceToken());
    }

    @Transactional
    public OAuthTokenResponse generateOAuthTokenResponse(Platform platform, String platformId, String name, String deviceToken) {
        return userRepository.findIdByPlatformAndPlatformId(platform, platformId)
                .map(userId ->  {
                    User findUser = userRepository.findById(userId)
                            .orElseThrow(() -> new NotFoundUserException());
                    String accessToken = jwtTokenProvider.createAccessToken(findUser.getId());
                    String refreshToken = jwtTokenProvider.createRefreshToken();
                    jwtTokenProvider.updateRefreshToken(userId, refreshToken);

                    alarmService.updateDeviceToken(findUser, deviceToken);
                    return new OAuthTokenResponse(accessToken, refreshToken,true, platformId, userId);
                })
                .orElseGet(() -> {
                    User saveUser = userRepository.save(new User(name, platform, platformId, deviceToken));
                    String accessToken = jwtTokenProvider.createAccessToken(saveUser.getId());
                    String refreshToken = jwtTokenProvider.createRefreshToken();
                    jwtTokenProvider.updateRefreshToken(saveUser.getId(), refreshToken);

                    return new OAuthTokenResponse(accessToken, refreshToken,false, platformId, saveUser.getId());
                });
    }
}
