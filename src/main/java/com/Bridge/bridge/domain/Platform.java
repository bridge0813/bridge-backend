package com.Bridge.bridge.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Platform {

    APPLE("apple"),
    KAKAO("kakao");

    private String platform;

}
