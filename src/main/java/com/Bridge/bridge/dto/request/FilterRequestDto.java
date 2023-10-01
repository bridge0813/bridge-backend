package com.Bridge.bridge.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FilterRequestDto {

    private String part;

    private List<String> skills;

    @Builder
    public FilterRequestDto(String part, List<String> skills) {
        this.part = part;
        this.skills = skills;
    }
}
