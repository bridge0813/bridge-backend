package com.Bridge.bridge.security;

import com.Bridge.bridge.domain.Platform;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.exception.unauthorized.InvalidTokenException;
import com.Bridge.bridge.exception.unauthorized.TokenExpiredException;
import com.Bridge.bridge.repository.UserRepository;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtTokenProviderTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("유효한 JWT 토큰 셍성")
    void createJWT() {
        //given
        Long id = 1L;

        //when
        String token = jwtTokenProvider.createAccessToken(id);

        //then
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    @DisplayName("올바른 Payload 값으로 토큰 생성")
    void createAccessToken() {
        //given
        Long id = 2L;

        //when
        String token = jwtTokenProvider.createAccessToken(id);

        //then
        JwtParser parser = Jwts.parserBuilder().setSigningKey(jwtTokenProvider.getKey()).build();
        assertEquals("2", parser.parseClaimsJws(token).getBody().getSubject());
    }

    @Test
    @DisplayName("유효한 JWT 토큰 셍성 - 리프레쉬")
    void createJWT_Refresh() {
        //when
        String token = jwtTokenProvider.createRefreshToken();

        //then
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    @DisplayName("올바른 Payload 값으로 리프레쉬 토큰 생성")
    void createRefreshToken() {

        //when
        String token = jwtTokenProvider.createRefreshToken();

        //then
        JwtParser parser = Jwts.parserBuilder().setSigningKey(jwtTokenProvider.getKey()).build();
        assertEquals("RefreshToken", parser.parseClaimsJws(token).getBody().getSubject());
    }

    @Test
    @DisplayName("리프레쉬 토큰 업데이트")
    void updateRefreshToken() {
        //given
        User user = new User("bridge", "bridge@apple.com", Platform.APPLE, "11");
        User saveUser = userRepository.save(user);

        String token = Jwts.builder()
                .setSubject("bridge")
                .signWith(SignatureAlgorithm.HS256,jwtTokenProvider.getKey())
                .compact();

        //when
        jwtTokenProvider.updateRefreshToken(saveUser.getId(), token);

        //then
        User findUser = userRepository.findAll().get(0);
        assertEquals(token, findUser.getRefreshToken());
    }

    @Test
    @DisplayName("잘못된 형식의 JWT 예외 반환")
    void validateToken() {

        //given
        String token = "invalid-token";

        //expected
        assertThrows(InvalidTokenException.class, () -> jwtTokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("만료된 토큰 검증 시 예외 반환")
    void validateExpiredToken() {
        //given
        Long id = 1L;
        Date now = new Date();
        Date expire = new Date(now.getTime() - 1);

        String invalidToken = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        //expected
        assertThrows(TokenExpiredException.class, () -> jwtTokenProvider.validateToken(invalidToken));
    }

    @Test
    @DisplayName("엑세스 토큰 추출")
    void extractAccessToken() {
        //given
        Long id = 1L;
        Date now = new Date();
        Date expire = new Date(now.getTime() + 1000);

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(id))
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        String refreshToken = "refresh-token";

        //when
        String token = jwtTokenProvider.extractAccessToken(setRequest(accessToken, refreshToken))
                .orElseThrow();

        //then
        assertEquals(token, accessToken);
    }

    @Test
    @DisplayName("리프레쉬 토큰 추출")
    void extractRefreshToken() {
        //given
        Date now = new Date();
        Date expire = new Date(now.getTime() + 1000);

        String accessToken = "access-token";

        String refreshToken = Jwts.builder()
                .setSubject("RefreshToken")
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        JwtParser parser = Jwts.parserBuilder().setSigningKey(jwtTokenProvider.getKey()).build();
        //when
        String token = jwtTokenProvider.extractRefreshToken(setRequest(accessToken, refreshToken))
                .orElseThrow();

        //then
        assertEquals(token, refreshToken);
    }

    @Test
    @DisplayName("토큰에서 payload 추출")
    void getPayload() {
        //given
        String sub = "accessToken";

        String token = Jwts.builder()
                .setSubject(sub)
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        //when
        String payload= jwtTokenProvider.getPayload(token);

        //then
        assertEquals(sub, payload);
    }

    @Test
    @DisplayName("유효하지 않은 토큰 payload 추출 예외 반환")
    void getPayloadExpire() {
        //given
        String sub = "accessToken";
        Date now = new Date();

        String token = Jwts.builder()
                .setSubject(sub)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() -1))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        //expected
        assertThrows(TokenExpiredException.class, () ->jwtTokenProvider.getPayload(token));
    }

    @Test
    @DisplayName("올바르지 않은 토큰 payload 추출 예외 반환")
    void getPayloadNotJWT() {
        //given
        String token = "accessToken";

        //expected
        assertThrows(InvalidTokenException.class, () ->jwtTokenProvider.getPayload(token));
    }

    @Test
    @DisplayName("리프레쉬 토큰 일치하는 경우")
    void matchRefreshToken() {
        //given
        User user = new User("bridge", "bridge@apple.com", Platform.APPLE, "22");

        String token = Jwts.builder()
                .setSubject("refresh")
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        user.updateRefreshToken(token);
        User saveUser = userRepository.save(user);

        //when
        Long userId = jwtTokenProvider.matchRefreshToken(token);

        //then
        assertEquals(saveUser.getId(), userId);
    }

    @Test
    @DisplayName("리프레쉬 토큰 일치하지 않아 예외 반환")
    void matchRefreshTokenException() {
        //given
        User user = new User("bridge", "bridge@apple.com", Platform.APPLE, "22");

        String token = Jwts.builder()
                .setSubject("refresh")
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        user.updateRefreshToken(token);
        userRepository.save(user);

        String fakeToken = "fake-refresh";
        //expected
        assertThrows(EntityNotFoundException.class, () ->jwtTokenProvider.matchRefreshToken(fakeToken));
    }

    private HttpServletRequest setRequest(String accessToken, String refreshToken) {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

        mockHttpServletRequest.addHeader("Authorization", "Bearer "+accessToken);
        mockHttpServletRequest.addHeader("Authorization-refresh", "Bearer "+refreshToken);

        return mockHttpServletRequest;
    }
}