package com.Bridge.bridge.security.apple;

import com.Bridge.bridge.dto.response.AppleMemberResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class AppleUtils {

    @Value("${apple.team.id}")
    private String APPLE_TEAM_ID;

    @Value("${apple.key.id}")
    private String APPLE_KEY_ID;

    @Value("${apple.client.id}")
    private String APPLE_CLIENT_ID;

    @Value("${apple.redirect.url}")
    private String APPLE_REDIRECT_URL;

    @Value("${apple.key.path}")
    private String APPLE_KEY_PATH;

    @Value("${apple.iss}")
    private String APPLE_ISS;

    private final static String APPLE_AUTH_URL = "https://appleid.apple.com/auth/authorize";

    private final ObjectMapper objectMapper;


    /**
     * 처음 로그인 시 애플로그인으로 이동할 수 있게 하는 URL
     * @return : apple 로그인 URL 반환
     */
    public String getAppleLogin() {
        return APPLE_AUTH_URL
                + "?client_id=" + APPLE_CLIENT_ID
                + "&redirect_uri=" + APPLE_REDIRECT_URL
                + "&response_type=code%20id_token&scope=name%20email&response_mode=form_post";
    }

    /**
     * ID Token 검증 후 유저 정보 가져오는 메소드
     * @param idToken
     */
    public AppleMemberResponse getAppleMember(String idToken) throws Exception {

        Map<String, String> headers = parseHeaders(idToken);
        ApplePublicKeys applePublicKeys = getPublicKey();
        PublicKey publicKey = generatePublicKeys(headers, applePublicKeys);

        Claims claims = passePublicKeyAndGetClaims(idToken, publicKey);
        if (!validateClaims(claims)) {
            throw new Exception("Claim 값이 올바르지 않음");
        }

        return new AppleMemberResponse(claims.getSubject(), claims.get("email", String.class));
    }

    /**
     * payload 검증 메소드
     * @param claims
     */
    private boolean validateClaims(Claims claims) {
        return claims.getIssuer().contains(APPLE_ISS) &&
                claims.getAudience().equals(APPLE_CLIENT_ID);
    }

    /**
     * ID Token 헤더 디코딩 메소드
     * @param idToken
     */
    private Map<String, String> parseHeaders(String idToken) throws IllegalAccessException {
        try {
            String encodedHeader = idToken.split("\\.")[0];
            String decodedHeader = new String(Base64Utils.decodeFromUrlSafeString(encodedHeader));
            return objectMapper.readValue(decodedHeader, Map.class);
        } catch (JsonProcessingException | ArrayIndexOutOfBoundsException e) {
            throw new IllegalAccessException();
        }
    }

    /**
     * 얻은 퍼블릭 키로 복호화하여 payload 값 얻는 메소드
     * @param idToken
     * @param publicKey
     */
    private Claims passePublicKeyAndGetClaims(String idToken, PublicKey publicKey) throws Exception {
        try {
            return Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(idToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new Exception("토큰 만료");
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new Exception("토큰이 이상홤");
        }
    }


    /**
     * 애플 공개키 얻기
     */
    public ApplePublicKeys getPublicKey() throws Exception {

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/json");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        HttpEntity<MultiValueMap<String, String>> applePublicKeys =
                new HttpEntity<>(params, headers);

        ResponseEntity<String> response = rt.exchange(
                "https://appleid.apple.com/auth/keys",
                HttpMethod.GET,
                applePublicKeys,
                String.class
        );

        try {
            ApplePublicKeys keys = objectMapper.readValue(response.getBody(), ApplePublicKeys.class);
            return keys;
        } catch (JsonProcessingException e) {
            throw new Exception();
        }
    }

    /**
     * 헤더와 일치하는 애플의 퍼블릭키 찾아서 퍼블릭키 생성
     * @param header
     * @param keys
     */
    private PublicKey generatePublicKeys(Map<String, String> header, ApplePublicKeys keys) throws Exception {
        ApplePublicKey publicKey = keys.getKeys().stream()
                .filter(k -> k.getAlg().equals(header.get("alg")) && k.getKid().equals(header.get("kid")))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Apple JWT 값의 alg, kid 정보가 올바르지 않습니다."));

        byte[] nBytes = Base64Utils.decodeFromUrlSafeString(publicKey.getN());
        byte[] eBytes = Base64Utils.decodeFromUrlSafeString(publicKey.getE());

        BigInteger n = new BigInteger(1, nBytes);
        BigInteger e = new BigInteger(1, eBytes);

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(publicKey.getKty());
            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new Exception("Apple OAuth 로그인 중 public key 생성에 문제가 발생했습니다.");
        }

    }
}
