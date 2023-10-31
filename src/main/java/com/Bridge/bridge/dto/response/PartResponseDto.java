package com.Bridge.bridge.dto.response;

import com.Bridge.bridge.domain.Stack;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class PartResponseDto {

    private String recruitPart;

    private int recruitNum;

    private List<String> recruitSkill;

    private String requirement;

    @Builder
    public PartResponseDto(String recruitPart, int recruitNum, List<Stack> recruitSkill, String requirement) {
        this.recruitPart = recruitPart;
        this.recruitNum = recruitNum;
        this.recruitSkill = recruitSkill.stream()
                .map(s -> s.getValue())
                .collect(Collectors.toList());
        this.requirement = requirement;
    }
}
