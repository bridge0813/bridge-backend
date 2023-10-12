package com.Bridge.bridge.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class MyProjectResponseDto {

    String recruitPart; // 모집 분야

    String requirement; // 바라는 점

    List<String> recruitSkill; // 모집 스킬

    int recruitNum; // 모집 인원 수

    @Builder
    public MyProjectResponseDto(String recruitPart, String requirement, List<String> recruitSkill, int recruitNum) {
        this.recruitPart = recruitPart;
        this.requirement = requirement;
        this.recruitSkill = recruitSkill;
        this.recruitNum = recruitNum;
    }
}
