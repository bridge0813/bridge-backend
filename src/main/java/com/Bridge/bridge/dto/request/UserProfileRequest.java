package com.Bridge.bridge.dto.request;

import com.Bridge.bridge.domain.Profile;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfileRequest {

    private String selfIntro;       // 자기소개서

    private String career;          // 경력 사항

    private List<String> stack;     // 스택

    @Builder
    public UserProfileRequest(String selfIntro, String career, List<String> stack) {
        this.selfIntro = selfIntro;
        this.career = career;
        this.stack = stack;
    }

    public Profile toEntity() {
        return Profile.builder()
                .selfIntro(selfIntro)
                .career(career)
                .skill(stack)
                .build();
    }
}
