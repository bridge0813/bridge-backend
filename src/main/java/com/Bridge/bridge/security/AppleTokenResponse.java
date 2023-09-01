package com.Bridge.bridge.security;

import lombok.Data;

@Data
public class AppleTokenResponse {

    private String accessToken;

    private String tokenType;

    private int expiresIn;

    private String refreshToken;

    private String idToken;

    public AppleTokenResponse(String accessToken, String tokenType, int expiresIn, String refreshToken, String idToken) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.idToken = idToken;
    }
}
