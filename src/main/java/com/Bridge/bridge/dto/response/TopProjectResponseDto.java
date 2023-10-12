package com.Bridge.bridge.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TopProjectResponseDto {

    int rank; // 인기 순위
    String title; // 모집글 제목
    LocalDateTime uploadTime; // 모집글 작성 날짜, 시간
//    String uploadTime;
//    int dDay;

    @Builder
    public TopProjectResponseDto(int rank, String title, LocalDateTime uploadTime) {
        this.rank = rank;
        this.title = title;
        this.uploadTime = uploadTime;
    }
}
