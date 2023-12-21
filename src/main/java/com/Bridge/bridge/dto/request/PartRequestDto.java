package com.Bridge.bridge.dto.request;

import com.Bridge.bridge.domain.Field;
import com.Bridge.bridge.domain.Part;
import com.Bridge.bridge.domain.Stack;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
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
                .recruitPart(Field.valueOf(this.getRecruitPart()))
                .recruitNum(this.getRecruitNum())
                .recruitSkill(this.getRecruitSkill().stream()
                        .map(s -> Stack.valueOf(s))
                        .collect(Collectors.toList()))
                .requirement(this.getRequirement())
                .build();
    }
}
