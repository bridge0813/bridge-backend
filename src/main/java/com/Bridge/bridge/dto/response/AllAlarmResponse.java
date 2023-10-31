package com.Bridge.bridge.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AllAlarmResponse {

    private Long id; // 알람 ID

    private String title; // 알림 제목

    private String content; // 알림 내용

    private LocalDateTime time;

    @Builder
    public AllAlarmResponse(Long id, String title, String content, LocalDateTime time) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.time = time;
    }
}
