package com.Bridge.bridge.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TopProjectResponseDto {

    int rank; // 인기 순위

    Long projectId; // 모집글ID

    String title; // 모집글 제목

    String dueDate; // 모집글 작성 날짜, 시간

    int recruitNum; // 총 모집 인원

    boolean scrap; // 스크랩 여부

    @Builder
    public TopProjectResponseDto(int rank, Long projectId, String title, String dueDate, int recruitNum, boolean scrap) {
        this.rank = rank;
        this.projectId = projectId;
        this.title = title;
        this.dueDate = dueDate;
        this.recruitNum = recruitNum;
        this.scrap = scrap;
    }
}
