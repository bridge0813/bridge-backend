package com.Bridge.bridge.dto.response;

import com.Bridge.bridge.domain.Project;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
public class BookmarkListResponse {

    private Long projectId;

    private String dDay;

    private String title;

    private LocalDateTime startDate;  // 프로젝트 시작

    private LocalDateTime endDate;  // 프로젝트 끝

    private int recruitTotalNum; // 총 모집 인원

    @Builder
    private BookmarkListResponse(Long projectId, String dDay, String title, LocalDateTime startDate, LocalDateTime endDate, int recruitTotalNum) {
        this.projectId = projectId;
        this.dDay = dDay;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.recruitTotalNum = recruitTotalNum;
    }

    public static BookmarkListResponse from(Project project) {
        int recruitTotalNum = project.getRecruit().stream()
                .mapToInt(p -> p.getRecruitNum())
                .sum();

        return BookmarkListResponse.builder()
                .projectId(project.getId())
                .dDay(getBetweenDays(project.getUploadTime(), project.getDueDate()))
                .title(project.getTitle())
                .startDate(setTime(project.getStartDate()))
                .endDate(setTime(project.getEndDate()))
                .recruitTotalNum(recruitTotalNum)
                .build();
    }

    private static LocalDateTime setTime(LocalDateTime time) {
        if (time != null) {
            return time;
        }
        return null;
    }

    private static String getBetweenDays(LocalDateTime start, LocalDateTime end) {
        return String.valueOf(ChronoUnit.DAYS.between(start, end));
    }
}
