package com.Bridge.bridge.dto.response;

import lombok.Data;

@Data
public class OAuthTokenResponse {

    private String grantType;

    private String accessToken;

    private String email;

    private boolean isRegistered;

    private String platformId;

    public OAuthTokenResponse(String accessToken, String email, boolean isRegistered, String platformId) {
        this.grantType = "Bearer";
        this.accessToken = accessToken;
        this.email = email;
        this.isRegistered = isRegistered;
        this.platformId = platformId;
    }
}
