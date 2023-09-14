package com.Bridge.bridge.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Data
public class UserSignUpRequest {

    @NotBlank
    private String platformId;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

}
