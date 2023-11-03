package com.Bridge.bridge.dto.response;

import com.Bridge.bridge.domain.Project;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookmarkListResponse {

    private Long projectId;

    private String title;

    private LocalDateTime dueDate;  // 프로젝트 모집 기간

    private int recruitTotalNum; // 총 모집 인원

    public BookmarkListResponse(Project project) {
        this.projectId = project.getId();
        this.title = project.getTitle();
        this.dueDate = project.getDueDate().toString();
        int recruitTotalNum = project.getRecruit().stream()
                .mapToInt(p -> p.getRecruitNum())
                .sum();
        this.recruitTotalNum = recruitTotalNum;
    }
}
