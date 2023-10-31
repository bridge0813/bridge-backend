package com.Bridge.bridge.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Field {

    IOS("iOS"),
    AOS("안드로이드"),
    FRONTEND("프론트엔드"),
    BACKEND("백엔드"),
    UIUX("UI/UX"),
    BIBX("BI/BX"),
    VIDEOMOTION("영상/모션"),
    PM("PM");

    private final String value;
}
