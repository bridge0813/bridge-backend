package com.Bridge.bridge.dto.response;

import com.Bridge.bridge.domain.ApplyProject;
import com.Bridge.bridge.domain.Project;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplyProjectResponse {

    private Long projectId; // 프로젝트 ID

    private String stage;   // 지원 상태

    private String title;   // 제목

    private String overview;    //개요

    private String dueDate;    // 모집 마감

    private ApplyProjectResponse(Project project, String stage) {
        this.projectId = project.getId();
        this.title = project.getTitle();
        this.overview = project.getOverview();
        this.dueDate = project.getDueDate().toString();
        this.stage = stage;
    }

    public static ApplyProjectResponse from(ApplyProject applyProject) {
        Project appliedProject = applyProject.getProject();
        return new ApplyProjectResponse(appliedProject, applyProject.getStage());
    }

}
