package com.Bridge.bridge.dto.response;

import com.Bridge.bridge.domain.Part;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class ProjectListResponseDto {

    private String title;                   // 제목

    private String startDate;               // 프로젝트 시작일

    private String endDate;                 // 프로젝트 종료일

    private List<Part> recruit;             // 모집 분야, 모집 인원

    @Builder
    public ProjectListResponseDto(String title, String startDate, String endDate, List<Part> recruit) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.recruit = recruit;
    }
}
