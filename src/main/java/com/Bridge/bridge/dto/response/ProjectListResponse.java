package com.Bridge.bridge.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class ProjectListResponse {

    private Long projectId;                 // 프로젝트 아이디

    private String title;                   // 제목

    private String dueDate;                 // 프로젝트 종료일

    private int recruitTotalNum;            // 총 모집 인원

    private boolean scrap;                  // 스크랩 여부

    @Builder
    public ProjectListResponse(Long projectId, String title, String dueDate, int recruitTotalNum, boolean scrap) {
        this.projectId = projectId;
        this.title = title;
        this.dueDate = dueDate;
        this.recruitTotalNum = recruitTotalNum;
        this.scrap = scrap;
    }
}
