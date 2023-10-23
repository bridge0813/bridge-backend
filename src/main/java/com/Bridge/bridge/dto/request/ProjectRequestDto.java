package com.Bridge.bridge.dto.request;

import com.Bridge.bridge.domain.Project;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.request.PartRequestDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ProjectRequestDto { // 모집글 생성 시 받아올 데이터 관련 dto

    private String title;           //제목

    private String overview;        // 개요, 프로젝트에 대한 간단한 소개

    private String dueDate;         //기간

    private String startDate;       // 프로젝트 시작일

    private String endDate;         // 프로젝트 종료일

    private List<PartRequestDto> recruit; // 모집 분야, 모집 인원

    private List<String> tagLimit;        //지원자 태그 제한록

    private String meetingWay;      //대면 or 비대면 여부

    private String stage;           // 진행 단계

    private Long userId;        // 모집글을 작성한 유저 ID

    @Builder
    public ProjectRequestDto(String title, String overview, String dueDate, String startDate, String endDate, List<PartRequestDto> recruit, List<String> tagLimit, String meetingWay, String stage, Long userId) {
        this.title = title;
        this.overview = overview;
        this.dueDate = dueDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.recruit = recruit;
        this.tagLimit = tagLimit;
        this.meetingWay = meetingWay;
        this.stage = stage;
        this.userId= userId;
    }

    public Project toEntityOfProject(User user){
        LocalDateTime localDateTime = LocalDateTime.now();

        int year = Integer.valueOf(this.getDueDate().substring(0,2));
        int month = Integer.valueOf(this.getDueDate().substring(2,4));
        int date = Integer.valueOf(this.getDueDate().substring(4,6));

        localDateTime.withYear(year);
        localDateTime.withMonth(month);
        localDateTime.withDayOfMonth(date);
        localDateTime.withHour(23);
        localDateTime.withMinute(59);
        localDateTime.withSecond(59);

        // 포맷
        String formatedNow = localDateTime.format(DateTimeFormatter.ofPattern("YYMMDDHHmmss"));

        return Project.builder()
                .title(this.getTitle())
                .overview(this.getOverview())
                .dueDate(this.getDueDate())
                .startDate(this.getStartDate())
                .endDate(formatedNow)
                .recruit(new ArrayList<>())
                .tagLimit(this.getTagLimit())
                .meetingWay(this.getMeetingWay())
                .stage(this.getStage())
                .user(user)
                .build();
    }
}
