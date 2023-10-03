package com.Bridge.bridge.dto.response;

import com.Bridge.bridge.domain.Part;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class ProjectListResponseDto {

    private Long projectId;                 // 프로젝트 아이디

    private String title;                   // 제목

    private String dueDate;                 // 프로젝트 종료일

    private int recruitTotalNum;             // 총 모집 인원

    @Builder
    public ProjectListResponseDto(Long projectId, String title, String dueDate, int recruitTotalNum) {
        this.projectId = projectId;
        this.title = title;
        this.dueDate = dueDate;
        this.recruitTotalNum = recruitTotalNum;
    }
}
