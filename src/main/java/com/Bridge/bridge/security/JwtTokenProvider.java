package com.Bridge.bridge.security;

import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.exception.notfound.NotFoundUserException;
import com.Bridge.bridge.exception.unauthorized.InvalidTokenException;
import com.Bridge.bridge.exception.unauthorized.TokenExpiredException;
import com.Bridge.bridge.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Getter
@Component
public class JwtTokenProvider {

    private static final Long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 30;        // ACCESS 토큰 만료 시간 (30분)
    private static final Long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 6;   // REFRESH 토큰 만료 시간 (6시간)

    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String ACCESS_TOKEN_HEADER = "Authorization";
    private static final String REFRESH_TOKEN_HEADER = "Authorization-refresh";

    private final Key key;

    private final UserRepository userRepository;

    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey, UserRepository userRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.userRepository = userRepository;
    }

    // 토큰 생성
    public String createAccessToken(Long userId) {
        Date now = new Date();
        Date validity = new Date((now.getTime() + ACCESS_TOKEN_EXPIRE_TIME));

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    // 리프레쉬 토큰 생성
    public String createRefreshToken() {
        Date now = new Date();
        Date validity = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(REFRESH_TOKEN_SUBJECT)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    // 리프레쉬 토큰 저장
    @Transactional
    public void updateRefreshToken(Long userId, String refreshToken) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        findUser.updateRefreshToken(refreshToken);
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("엑세스 토큰의 유효 시간이 지났습니다.");
            throw new TokenExpiredException();
        } catch (JwtException e) {
            log.info("올바르지 않은 형식의 토큰입니다.");
            throw new InvalidTokenException();
        }

    }

    // 엑세스 토큰 추출
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(ACCESS_TOKEN_HEADER)).filter(
                accessToken -> accessToken.startsWith(TOKEN_PREFIX)
        ).map(accessToken -> accessToken.replace(TOKEN_PREFIX, ""));
    }

    // 리프세쉬 토큰 추출
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(REFRESH_TOKEN_HEADER)).filter(
                refreshToken -> refreshToken.startsWith(TOKEN_PREFIX)
        ).map(refreshToken -> refreshToken.replace(TOKEN_PREFIX, ""));
    }

    // 토큰에서 정보 추출
    public String getPayload(String token) {
        try {
            return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }
    }

    // 리프레쉬 토큰 일치 여부
    public Long matchRefreshToken(String refreshToken) {
        User findUser = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new NotFoundUserException());

        return findUser.getId();
    }
}
