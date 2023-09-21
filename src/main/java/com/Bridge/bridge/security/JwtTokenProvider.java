package com.Bridge.bridge.security;

import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.Enumeration;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final Long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 30;        // ACCESS 토큰 만료 시간 (30분)
    private static final Long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 6;   // REFRESH 토큰 만료 시간 (6시간)

    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String TOKEN_PREFIX = "Bearer";
    private static final String AUTHORIZATION_HEADER = "Authorization";

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
    public void updateRefreshToken(Long userId, String refreshToken) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));


    }

    // 토큰 검증
    public void validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        }
        catch (SecurityException | MalformedJwtException e) {
            log.info("유효하지 않은 토큰입니다.");
        } catch (ExpiredJwtException e) {
            log.info("유효 시간이 지났습니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("토큰을 찾을 수 없습니다.");
        }
    }

    // 토큰 추출
    public String  extractAccessToken(HttpServletRequest request) throws Exception {
        Enumeration<String> headers = request.getHeaders(AUTHORIZATION_HEADER);
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if (value.toLowerCase().startsWith(TOKEN_PREFIX.toLowerCase())) {
                return value.substring(TOKEN_PREFIX.length() + 1);
            }
        }
        //TODO : 예외처리 필요! 토큰이 없는 경우
        throw new Exception();
    }
}
