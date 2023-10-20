package com.Bridge.bridge.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
public class NotificationRequestDto {
<<<<<<< HEAD
<<<<<<< HEAD
    Long userID;                // 유저 아이디
=======
    String deviceToken;         // 디바이스를 구분짓는 토큰
>>>>>>> 0bccd71 (FEAT : NotificationRequestDto 생성)
=======
    Long userID;                // 유저 아이디
>>>>>>> a345cec (FIX : deviceToken 속성 추가)

    String title;               // 알림 제목

    String body;                // 알림 내용

    @Builder
<<<<<<< HEAD
<<<<<<< HEAD
    public NotificationRequestDto(Long userID, String title, String body) {
        this.userID = userID;
=======
    public NotificationRequestDto(String deviceToken, String title, String body) {
        this.deviceToken = deviceToken;
>>>>>>> 0bccd71 (FEAT : NotificationRequestDto 생성)
=======
    public NotificationRequestDto(Long userID, String title, String body) {
        this.userID = userID;
>>>>>>> a345cec (FIX : deviceToken 속성 추가)
        this.title = title;
        this.body = body;
    }
}
