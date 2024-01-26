package com.Bridge.bridge.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class ProfileUpdateRequest {

    private String name;
    private String selfIntro;
    private String career;
    private List<FieldAndStackRequest> fieldAndStacks;
    private List<String> refLinks;
    private List<Long> fileIds;

    @Builder
    public ProfileUpdateRequest(String name, String selfIntro, String career, List<FieldAndStackRequest> fieldAndStacks, List<String> refLinks, List<Long> fileIds) {
        this.name = name;
        this.selfIntro = selfIntro;
        this.career = career;
        this.fieldAndStacks = fieldAndStacks;
        this.refLinks = refLinks;
        this.fileIds = fileIds;
    }
}
