package com.Bridge.bridge.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FilterRequest {

    private String part;

    private List<String> skills;

    @Builder
    public FilterRequest(String part, List<String> skills) {
        this.part = part;
        this.skills = skills;
    }
}
