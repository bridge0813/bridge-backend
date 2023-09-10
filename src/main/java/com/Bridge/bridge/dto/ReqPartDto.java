package com.Bridge.bridge.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class ReqPartDto {

    private String recruitPart;

    private int recruitNum;

    private List<String> recruitSkill;

    private String requirement;

    @Builder
    public ReqPartDto(String recruitPart, int recruitNum, List<String> recruitSkill, String requirement) {
        this.recruitPart = recruitPart;
        this.recruitNum = recruitNum;
        this.recruitSkill = recruitSkill;
        this.requirement = requirement;
    }
}
