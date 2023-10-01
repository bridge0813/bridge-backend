package com.Bridge.bridge.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class PartResponseDto {

    private String recruitPart;

    private int recruitNum;

    private List<String> recruitSkill;

    private String requirement;

    @Builder
    public PartResponseDto(String recruitPart, int recruitNum, List<String> recruitSkill, String requirement) {
        this.recruitPart = recruitPart;
        this.recruitNum = recruitNum;
        this.recruitSkill = recruitSkill;
        this.requirement = requirement;
    }
}
