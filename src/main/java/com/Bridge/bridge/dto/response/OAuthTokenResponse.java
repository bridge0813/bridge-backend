package com.Bridge.bridge.dto.response;

import lombok.Data;

@Data
public class OAuthTokenResponse {

    private String grantType;

    private String accessToken;

    private String refreshToken;

    private String email;

    private boolean isRegistered;

    private String platformId;

    public OAuthTokenResponse(String accessToken, String refreshToken, String email, boolean isRegistered, String platformId) {
        this.grantType = "Bearer";
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.email = email;
        this.isRegistered = isRegistered;
        this.platformId = platformId;
    }
}
