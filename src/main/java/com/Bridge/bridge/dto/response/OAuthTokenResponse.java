package com.Bridge.bridge.dto.response;

import lombok.Data;

@Data
public class OAuthTokenResponse {

    private String grantType;

    private String accessToken;

    private String refreshToken;


    private boolean isRegistered;

    private String platformId;

    private Long userId;

    public OAuthTokenResponse(String accessToken, String refreshToken, boolean isRegistered, String platformId, Long userId) {
        this.grantType = "Bearer";
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.isRegistered = isRegistered;
        this.platformId = platformId;
        this.userId = userId;
    }
}
