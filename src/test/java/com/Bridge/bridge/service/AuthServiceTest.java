package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Platform;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.response.OAuthTokenResponse;
import com.Bridge.bridge.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;


    @AfterEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인 후 회원가입 - 처음 가입하는 경우")
    void signup() {
        //given
        String email = "bridge@apple.com";
        String platformId = "1";
        String name = "bridge";

        //when
        OAuthTokenResponse oAuthTokenResponse = authService.generateOAuthTokenResponse(email, Platform.APPLE, platformId, name);

        //then
        assertEquals(1L, userRepository.count());
        assertTrue(oAuthTokenResponse.getAccessToken().length() > 0);
        assertTrue(oAuthTokenResponse.getRefreshToken().length() > 0);
        assertEquals("bridge@apple.com", oAuthTokenResponse.getEmail());
        assertEquals(false, oAuthTokenResponse.isRegistered());
        assertEquals("1", oAuthTokenResponse.getPlatformId());
    }

    @Test
    @DisplayName("로그인 후 회원가입 - 이미 가입한 경우")
    void signin() {
        //given
        String email = "bridge@apple.com";
        String platformId = "11";
        String name = "bridge";

        userRepository.save(new User("bridge", "bridge@apple.com", Platform.APPLE, "11"));

        //when
        OAuthTokenResponse oAuthTokenResponse = authService.generateOAuthTokenResponse(email, Platform.APPLE, platformId, name);

        //then
        assertEquals(1L, userRepository.count());
        assertTrue(oAuthTokenResponse.getAccessToken().length() > 0);
        assertTrue(oAuthTokenResponse.getRefreshToken().length() > 0);
        assertEquals("bridge@apple.com", oAuthTokenResponse.getEmail());
        assertEquals(true, oAuthTokenResponse.isRegistered());
        assertEquals("11", oAuthTokenResponse.getPlatformId());
    }
}