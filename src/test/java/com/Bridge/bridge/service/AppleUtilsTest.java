package com.Bridge.bridge.service;

import com.Bridge.bridge.security.apple.ApplePublicKey;
import com.Bridge.bridge.security.apple.ApplePublicKeys;
import com.Bridge.bridge.security.apple.AppleUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class AppleUtilsTest {

    @Autowired
    private AppleUtils appleUtils;

    @Test
    @DisplayName("토큰 헤더 파싱 확인")
    void test() throws NoSuchAlgorithmException, IllegalAccessException {
        //given
        Date now = new Date();
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA")
                .generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();

        String idToken = Jwts.builder()
                .setHeaderParam("kid", "W2R4HXF3K")
                .claim("id", "12345678")
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setExpiration(new Date(now.getTime() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();

        //when
        Map<String, String> header = appleUtils.parseHeaders(idToken);

        //then
        assertEquals(true, header.containsKey("alg"));
        assertEquals(true, header.containsKey("kid"));
        assertEquals("RS256", header.get("alg"));
        assertEquals("W2R4HXF3K", header.get("kid"));
    }

    @Test
    @DisplayName("이상한 토큰 헤더 파싱 시 예외 반환")
    void test2() {
        //expected
        Assertions.assertThrows(IllegalAccessException.class, () -> appleUtils.parseHeaders("invalidToken"));
    }

    @Test
    @DisplayName("퍼블릭 키로 복호화하여 payload 값 파싱")
    void test3() throws Exception {
        //given
        Date now = new Date();
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA")
                .generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        String idToken = Jwts.builder()
                .setHeaderParam("kid", "W2R4HXF3K")
                .claim("id", "12345678")
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setExpiration(new Date(now.getTime() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();

        //when
        Claims claims = appleUtils.parsePublicKeyAndGetClaims(idToken, publicKey);

        //then
        assertEquals("12345678", claims.get("id"));
        assertEquals("aud", claims.getAudience());
        assertEquals("iss", claims.getIssuer());
    }

    @Test
    @DisplayName("만료된 토큰 payload 값 파싱 시 예외 반환")
    void test4() throws Exception{
        //given
        Date now = new Date();
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA")
                .generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        String idToken = Jwts.builder()
                .setHeaderParam("kid", "W2R4HXF3K")
                .claim("id", "12345678")
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setExpiration(new Date(now.getTime() - 1L))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();

        //expected
        assertThrows(Exception.class, () -> appleUtils.parsePublicKeyAndGetClaims(idToken, publicKey));
    }

    @Test
    @DisplayName("안맞는 키로 payload 값 파싱 시 예외 반환")
    void test5() throws Exception{
        //given
        Date now = new Date();
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA")
                .generateKeyPair();
        KeyPair diffKeyPair = KeyPairGenerator.getInstance("RSA")
                .generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = diffKeyPair.getPublic();

        String idToken = Jwts.builder()
                .setHeaderParam("kid", "W2R4HXF3K")
                .claim("id", "12345678")
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setExpiration(new Date(now.getTime() - 1L))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();

        //expected
        assertThrows(Exception.class, () -> appleUtils.parsePublicKeyAndGetClaims(idToken, publicKey));
    }

    @Test
    @DisplayName("애플 서버로 부터 Private Key 받아오기")
    void test6() throws Exception {
        //given
        ApplePublicKeys publicKeys = appleUtils.getPublicKey();
        List<ApplePublicKey> keys = publicKeys.getKeys();

        //when
        ApplePublicKey applePublicKey = keys.get(0);

        //then
        assertNotNull(applePublicKey.getKty());
        assertNotNull(applePublicKey.getKid());
        assertNotNull(applePublicKey.getUse());
        assertNotNull(applePublicKey.getAlg());
        assertNotNull(applePublicKey.getN());
        assertNotNull(applePublicKey.getE());
    }
}