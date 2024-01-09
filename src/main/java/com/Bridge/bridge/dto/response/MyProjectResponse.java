package com.Bridge.bridge.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class MyProjectResponse {

    private Long projectId;                 // 프로젝트 아이디

    private String title;                   // 제목

    private String overview;                // 내용

    private String dueDate;                 // 프로젝트 종료일

    private int recruitTotalNum;             // 총 모집 인원

    private String status;                 // 현재 모집 상황 -> 0 : 마감, 1 : 모집중

    @Builder
    public MyProjectResponse(Long projectId, String title, String overview, String dueDate, int recruitTotalNum, String status) {
        this.projectId = projectId;
        this.title = title;
        this.overview = overview;
        this.dueDate = dueDate;
        this.recruitTotalNum = recruitTotalNum;
        this.status = status;
    }
}

