package com.Bridge.bridge.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.util.List;

@Data
@NoArgsConstructor
public class UserProfileResponse {
    private String name;       // 이름

    private String profilePhotoURL;  // 유저 이미지

    private String selfIntro;   // 자기소개

    private List<String> fields;    // 분야

    private List<String> stacks;    // 기술 스택

    private String career;          // 경력 사항

    private String refLink;         // 참고 링크

    private List<FileResponse> refFiles;         // 첨부 파일

    @Builder
    public UserProfileResponse(String name, String profilePhotoURL, String selfIntro, List<String> fields, List<String> stacks, String career, String refLink, List<FileResponse> refFiles) {
        this.name = name;
        this.profilePhotoURL = profilePhotoURL;
        this.selfIntro = selfIntro;
        this.fields = fields;
        this.stacks = stacks;
        this.career = career;
        this.refLink = refLink;
        this.refFiles = refFiles;
    }
}
