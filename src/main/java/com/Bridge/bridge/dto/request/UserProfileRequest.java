package com.Bridge.bridge.dto.request;

import com.Bridge.bridge.domain.Profile;
import com.Bridge.bridge.domain.Stack;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfileRequest {


    private String selfIntro;       // 자기소개서

    private String career;          // 경력 사항

    private List<String> stack;     // 스택

    private String refLink;         // 침고 링크

    @Builder
    public UserProfileRequest(String selfIntro, String career, List<String> stack, String refLink) {
        this.selfIntro = selfIntro;
        this.career = career;
        this.stack = stack;
        this.refLink = refLink;
    }

    public Profile toEntity() {
        return Profile.builder()
                .refLink(refLink)
                .selfIntro(selfIntro)
                .career(career)
                .skill(stack.stream()
                        .map(s -> Stack.valueOf(s))
                        .collect(Collectors.toList()))
                .build();
    }
}
