package com.Bridge.bridge.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRegisterRequest {

    private Long userId;

    private UserFieldRequest userField;

    private UserProfileRequest userProfile;

    @Builder
    public UserRegisterRequest(Long userId, UserFieldRequest userField, UserProfileRequest userProfile) {
        this.userId = userId;
        this.userField = userField;
        this.userProfile = userProfile;
    }
}
