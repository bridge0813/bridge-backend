package com.Bridge.bridge.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
public class NotificationRequest {

    Long userId;                // 유저 아이디

    String deviceToken;         // 디바이스를 구분짓는 토큰

    String title;               // 알림 제목

    String body;                // 알림 내용

    @Builder
    public NotificationRequest(Long userId, String title, String body) {
        this.userId = userId;
        this.title = title;
        this.body = body;
    }
}
