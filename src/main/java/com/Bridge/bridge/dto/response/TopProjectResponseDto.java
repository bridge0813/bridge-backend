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

    String dueDate; // 모집글 작성 날짜, 시간

    @Builder
    public TopProjectResponseDto(int rank, String title, String dueDate) {
        this.rank = rank;
        this.title = title;
        this.dueDate = dueDate;
    }
}
