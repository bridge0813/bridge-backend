package com.Bridge.bridge.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class imminentProjectResponse {

    int imminentRank; // 마감일이 빠른 순위

    Long projectId; // 모집글ID

    String title; // 모집글 제목

    String dueDate; // 모집글 작성 날짜, 시간

    int recruitNum; // 총 모집인원

    boolean scrap;  // 스크랩 여부

    @Builder
    public imminentProjectResponse(int imminentRank, Long projectId, String title, String dueDate, int recruitNum, boolean scrap) {
        this.imminentRank = imminentRank;
        this.projectId = projectId;
        this.title = title;
        this.dueDate = dueDate;
        this.recruitNum = recruitNum;
        this.scrap = scrap;
    }
}
