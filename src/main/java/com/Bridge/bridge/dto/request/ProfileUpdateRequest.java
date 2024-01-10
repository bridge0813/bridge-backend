package com.Bridge.bridge.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
public class ProfileUpdateRequest {

    private String selfIntro;

    private String career;

    private List<String> stack;

    private String refLink;

    private List<Long> fileIds;

    @Builder
    public ProfileUpdateRequest(String selfIntro, String career, List<String> stack, String refLink, List<Long> fileIds) {
        this.selfIntro = selfIntro;
        this.career = career;
        this.stack = stack;
        this.refLink = refLink;
        this.fileIds = fileIds;
    }
}
