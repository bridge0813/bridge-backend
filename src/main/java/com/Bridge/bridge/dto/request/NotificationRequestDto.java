package com.Bridge.bridge.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
public class NotificationRequestDto {
    String deviceToken;         // 디바이스를 구분짓는 토큰

    String title;               // 알림 제목

    String body;                // 알림 내용

    @Builder
    public NotificationRequestDto(String deviceToken, String title, String body) {
        this.deviceToken = deviceToken;
        this.title = title;
        this.body = body;
    }
}
