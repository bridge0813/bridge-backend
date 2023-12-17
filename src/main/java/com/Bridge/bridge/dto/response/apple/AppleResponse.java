package com.Bridge.bridge.dto.response.apple;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AppleResponse {

    private String name;

    @NotBlank(message = "id token을 입력해 주세요!")
    private String idToken;

    @NotBlank(message = "device token을 입력해 주세요!")
    private String deviceToken;
}
