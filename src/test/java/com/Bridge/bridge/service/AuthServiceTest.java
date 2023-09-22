package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.response.OAuthTokenResponse;
import com.Bridge.bridge.dto.response.apple.AppleTokenResponse;
import com.Bridge.bridge.repository.UserRepository;
import com.Bridge.bridge.security.apple.AppleToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private AppleToken appleToken;

    @AfterEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인 후 회원가입 - 처음 가입하는 경우")
    void signup() {
        //given
        String email = "kyukyu@apple.com";
        String platformId = "1";
        String code = "12341234";

        AppleTokenResponse response = new AppleTokenResponse("token", "access", 4, "refreshToken", "idToken");

        when(appleToken.getAccessToken(any())).thenReturn(response);

        //when
        OAuthTokenResponse oAuthTokenResponse = authService.generateOAuthTokenResponse(email, platformId, code);

        //then
        assertEquals(1L, userRepository.count());
        assertEquals("token", oAuthTokenResponse.getAccessToken());
        assertEquals("kyukyu@apple.com", oAuthTokenResponse.getEmail());
        assertEquals(false, oAuthTokenResponse.isRegistered());
        assertEquals("1", oAuthTokenResponse.getPlatformId());
    }

    @Test
    @DisplayName("로그인 후 회원가입 - 이미 가입한 경우")
    void signup2() {
        //given
        String email = "kyukyu@apple.com";
        String platformId = "1";
        String code = "12341234";

        userRepository.save(new User("kyukyu@apple.com", "1"));

        AppleTokenResponse response = new AppleTokenResponse("token", "access", 4, "refreshToken", "idToken");

        when(appleToken.getAccessToken(any())).thenReturn(response);

        //when
        OAuthTokenResponse oAuthTokenResponse = authService.generateOAuthTokenResponse(email, platformId, code);

        //then
        assertEquals(1L, userRepository.count());
        assertEquals("token", oAuthTokenResponse.getAccessToken());
        assertEquals("kyukyu@apple.com", oAuthTokenResponse.getEmail());
        assertEquals(true, oAuthTokenResponse.isRegistered());
        assertEquals("1", oAuthTokenResponse.getPlatformId());
    }
}