package com.Bridge.bridge.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
public class NotificationRequestDto {
    Long userID;                // 유저 아이디

    String title;               // 알림 제목

    String body;                // 알림 내용

    @Builder
    public NotificationRequestDto(Long userID, String title, String body) {
        this.userID = userID;
        this.title = title;
        this.body = body;
    }
}
