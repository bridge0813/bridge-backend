package com.Bridge.bridge.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AlarmResponse {

    private Long alarmId;       // 알람 아이디

    private String title;       // 알람 제목

    private String content;     // 알람 내용

    private String time;        // 알람이 보내진 시간

    @Builder
    public AlarmResponse(Long alarmId, String title, String content, String time) {
        this.alarmId = alarmId;
        this.title = title;
        this.content = content;
        this.time = time;
    }
}
