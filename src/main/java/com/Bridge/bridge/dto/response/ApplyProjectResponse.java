package com.Bridge.bridge.dto.response;

import com.Bridge.bridge.domain.Project;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplyProjectResponse {

    private String stage;   // 지원 상태

    private String title;   // 제목

    private String overview;    //개요

    private String dueDate;    // 모집 마감

    public ApplyProjectResponse(Project project, String stage) {
        this.title = project.getTitle();
        this.overview = project.getOverview();
        this.dueDate = project.getDueDate();
        this.stage = stage;
    }
}
