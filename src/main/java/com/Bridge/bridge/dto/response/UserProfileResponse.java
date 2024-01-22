package com.Bridge.bridge.dto.response;

import com.Bridge.bridge.domain.Field;
import com.Bridge.bridge.domain.Stack;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class UserProfileResponse {
    private String name;       // 이름

    private String profilePhotoURL;  // 유저 이미지

    private String selfIntro;   // 자기소개

    private List<FieldAndStackResponse> fieldAndStacks; // 분야 별 기술 스택

    private String career;          // 경력 사항

    private List<String> refLinks;         // 참고 링크

    private List<FileResponse> refFiles;         // 첨부 파일

    @Builder
    public UserProfileResponse(String name, String profilePhotoURL, String selfIntro, List<FieldAndStackResponse> fieldAndStacks, String career, List<String> refLinks, List<FileResponse> refFiles) {
        this.name = name;
        this.profilePhotoURL = profilePhotoURL;
        this.selfIntro = selfIntro;
        this.fieldAndStacks = fieldAndStacks;
        this.career = career;
        this.refLinks = refLinks;
        this.refFiles = refFiles;
    }
}
