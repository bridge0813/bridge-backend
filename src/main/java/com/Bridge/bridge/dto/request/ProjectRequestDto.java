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

        int d_year = Integer.valueOf(this.getDueDate().substring(0,4));
        int d_month = Integer.valueOf(this.getDueDate().substring(5,7));
        int d_date = Integer.valueOf(this.getDueDate().substring(8,10));

        LocalDateTime dueDate = LocalDateTime.of(d_year,d_month,d_date,23,59,59);
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        if(this.startDate != null){
            int s_year = Integer.valueOf(this.getStartDate().substring(0,4));
            int s_month = Integer.valueOf(this.getStartDate().substring(5,7));
            int s_date = Integer.valueOf(this.getStartDate().substring(8,10));

            startDate = LocalDateTime.of(s_year,s_month,s_date,23,59,59);
        }

        if(this.endDate != null){
            int e_year = Integer.valueOf(this.getEndDate().substring(0,4));
            int e_month = Integer.valueOf(this.getEndDate().substring(5,7));
            int e_date = Integer.valueOf(this.getEndDate().substring(8,10));

            endDate = LocalDateTime.of(e_year,e_month,e_date,23,59,59);
        }

        LocalDateTime uploadTime = LocalDateTime.now();

        return Project.builder()
                .title(this.getTitle())
                .overview(this.getOverview())
                .dueDate(dueDate)
                .startDate(startDate)
                .endDate(endDate)
                .uploadTime(uploadTime)
                .recruit(new ArrayList<>())
                .tagLimit(this.getTagLimit())
                .meetingWay(this.getMeetingWay())
                .stage(this.getStage())
                .user(user)
                .build();
    }
}
