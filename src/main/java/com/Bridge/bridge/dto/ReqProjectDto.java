package com.Bridge.bridge.dto;

import com.Bridge.bridge.domain.Part;
import com.Bridge.bridge.domain.User;
import com.nimbusds.jose.shaded.gson.JsonObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ReqProjectDto { // 모집글 생성 시 받아올 데이터 관련 dto

    private String title;           //제목

    private String overview;        // 개요, 프로젝트에 대한 간단한 소개

    private String dueDate;         //기간

    private String startDate;       // 프로젝트 시작일

    private String endDate;         // 프로젝트 종료일

    private List<JsonObject> recruit; // 모집 분야, 모집 인원

    private List<String> tagLimit;        //지원자 태그 제한록

    private String meetingWay;      //대면 or 비대면 여부

    private String stage;           // 진행 단계

    private String useremail;        // 모집글을 작성한 유저 이메일


}
