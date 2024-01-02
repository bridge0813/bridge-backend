package com.Bridge.bridge.dto.response;

import com.Bridge.bridge.domain.Project;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
public class BookmarkListResponse {

    private Long projectId;

    private long dDay;

    private String title;

    private LocalDateTime startDate;  // 프로젝트 시작

    private LocalDateTime endDate;  // 프로젝트 끝

    private int recruitTotalNum; // 총 모집 인원

    public BookmarkListResponse(Project project) {
        this.projectId = project.getId();
        this.dDay = getBetweenDays(project.getUploadTime(), project.getDueDate());
        this.title = project.getTitle();
        this.startDate = setTime(project.getStartDate());
        this.endDate = setTime(project.getEndDate());
        int recruitTotalNum = project.getRecruit().stream()
                .mapToInt(p -> p.getRecruitNum())
                .sum();
        this.recruitTotalNum = recruitTotalNum;
    }

    private LocalDateTime setTime(LocalDateTime time) {
        if (time != null) {
            return time;
        }
        return null;
    }

    private long getBetweenDays(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.DAYS.between(start, end);
    }
}
