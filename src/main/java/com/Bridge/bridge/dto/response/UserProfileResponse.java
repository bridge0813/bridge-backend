package com.Bridge.bridge.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserProfileResponse {
    private String name;       // 이름

    private String selfIntro;   // 자기소개

    private List<String> fields;    // 분야

    private List<String> stacks;    // 기술 스택

    private String career;          // 경력 사항

    private String refLink;         // 참고 링크

    //TODO : 파일명 및 ID 반환으로 바꾸기
    private String refFile;         // 첨부 파일

    @Builder
    public UserProfileResponse(String name, String selfIntro, List<String> fields, List<String> stacks, String career, String refLink, String refFile) {
        this.name = name;
        this.selfIntro = selfIntro;
        this.fields = fields;
        this.stacks = stacks;
        this.career = career;
        this.refLink = refLink;
        this.refFile = refFile;
    }
}
