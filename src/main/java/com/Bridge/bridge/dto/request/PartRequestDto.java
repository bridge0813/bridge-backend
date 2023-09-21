package com.Bridge.bridge.dto.request;

import com.Bridge.bridge.domain.Part;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class PartRequestDto {

    private String recruitPart;

    private int recruitNum;

    private List<String> recruitSkill;

    private String requirement;

    @Builder
    public PartRequestDto(String recruitPart, int recruitNum, List<String> recruitSkill, String requirement) {
        this.recruitPart = recruitPart;
        this.recruitNum = recruitNum;
        this.recruitSkill = recruitSkill;
        this.requirement = requirement;
    }

    public Part toEntity(){
        return Part.builder()
                .recruitPart(this.getRecruitPart())
                .recruitNum(this.getRecruitNum())
                .recruitSkill(this.getRecruitSkill())
                .requirement(this.getRequirement())
                .build();
    }
}
