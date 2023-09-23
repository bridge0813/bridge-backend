package com.Bridge.bridge.security;

import com.Bridge.bridge.dto.response.TokenResponse;
import com.Bridge.bridge.exception.unauthorized.InvalidBearerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    private final ObjectMapper objectMapper;

    public LoginInterceptor(JwtTokenProvider jwtTokenProvider, ObjectMapper objectMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isPreflight(request)) {
            return true;
        }

        String refreshToken = jwtTokenProvider
                .extractRefreshToken(request)
                .filter(jwtTokenProvider::validateToken)
                .orElse(null);
        // 1. 리프레쉬 토큰이 있는 경우
        // 엑세스 토큰 재발급
        if (refreshToken != null) {
            Long userId = jwtTokenProvider.matchRefreshToken(refreshToken);
            String token = jwtTokenProvider.createAccessToken(userId);

            TokenResponse tokenResponse = new TokenResponse(token);
            String toJson = objectMapper.writeValueAsString(tokenResponse);

            response.setContentType("application/json");
            response.getWriter().write(toJson);
            return false;
        }

        // 2. 리프레쉬 토큰이 없는 경우
        // 2-1. 엑세스 토큰이 있는 경우
        // 인증 완료
        String accessToken = jwtTokenProvider
                .extractAccessToken(request)
                .filter(jwtTokenProvider::validateToken)
                .orElse(null);

        if (accessToken != null) {
            return true;
        }

        //2-2. 엑세스 토큰이 없는 경우
        //접근 권한 없음 (로그인 필요)
        throw new InvalidBearerException();
    }

    private boolean isPreflight(HttpServletRequest request) {
        return request.getMethod().equals(HttpMethod.OPTIONS.toString());
    }

}
