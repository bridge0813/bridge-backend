package com.Bridge.bridge.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FilterRequest {

    private long userId;

    private String part;

    private List<String> skills;

    @Builder
    public FilterRequest(long userId, String part, List<String> skills) {
        this.userId = userId;
        this.part = part;
        this.skills = skills;
    }
}
