package com.Bridge.bridge.security.apple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ApplePublicKeysTest {

    @Test
    @DisplayName("alg, kid 값을 받아 일치하는 public key 반환")
    void getMatchesKey() {
        //given
        ApplePublicKey expectedKey = new ApplePublicKey("kty", "kid", "use", "alg", "n", "e");
        ApplePublicKeys applePublicKeys = new ApplePublicKeys(List.of(expectedKey));

        //when
        ApplePublicKey matchesKey = applePublicKeys.getMatchesKey("alg", "kid");

        //then
        assertEquals(expectedKey, matchesKey);
    }

    @Test
    @DisplayName("일치하지 않아서 예외 반환")
    void getException() {
        //given
        ApplePublicKey expectedKey = new ApplePublicKey("kty", "kid", "use", "alg", "n", "e");
        ApplePublicKeys applePublicKeys = new ApplePublicKeys(List.of(expectedKey));

        //expected
        Assertions.assertThrows(IllegalArgumentException.class, () -> applePublicKeys.getMatchesKey("aaa", "bbb"));
    }
}