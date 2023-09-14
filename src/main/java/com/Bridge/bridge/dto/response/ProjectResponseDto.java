package com.Bridge.bridge.dto.response;

import com.Bridge.bridge.dto.PartRequestDto;
import com.Bridge.bridge.dto.ProjectRequestDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class ProjectResponseDto {

    private String title;           //제목

    private String overview;        // 개요, 프로젝트에 대한 간단한 소개

    private String dueDate;         //기간

    private String startDate;       // 프로젝트 시작일

    private String endDate;         // 프로젝트 종료일

    private List<PartResponseDto> recruit; // 모집 분야, 모집 인원

    private List<String> tagLimit;        //지원자 태그 제한록

    private String meetingWay;      //대면 or 비대면 여부

    private String stage;           // 진행 단계

    private String userName;        // 모집글을 작성한 유저 이름

    @Builder
    public ProjectResponseDto(String title, String overview, String dueDate, String startDate, String endDate, List<PartResponseDto> recruit, List<String> tagLimit, String meetingWay, String stage, String userName) {
        this.title = title;
        this.overview = overview;
        this.dueDate = dueDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.recruit = recruit;
        this.tagLimit = tagLimit;
        this.meetingWay = meetingWay;
        this.stage = stage;
        this.userName = userName;
    }


}
