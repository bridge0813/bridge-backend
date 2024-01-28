package com.Bridge.bridge.dto.request;

import com.Bridge.bridge.domain.Field;
import com.Bridge.bridge.domain.FieldAndStack;
import com.Bridge.bridge.domain.Profile;
import com.Bridge.bridge.domain.Stack;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfileRequest {


    private String selfIntro;       // 자기소개서

    private String career;          // 경력 사항

    private List<FieldAndStackRequest> fieldAndStacks;     // 필드와 스택

    private List<String> refLinks;         // 침고 링크

    @Builder
    public UserProfileRequest(String selfIntro, String career, List<FieldAndStackRequest> fieldAndStacks, List<String> refLinks) {
        this.selfIntro = selfIntro;
        this.career = career;
        this.fieldAndStacks = fieldAndStacks;
        this.refLinks = refLinks;
    }

    public Profile toEntity() {
        if (refLinks == null) {
            refLinks = new ArrayList<>();
        }

        //필드 + 스택 조합 저장
        List<FieldAndStack> fieldAndStackList = new ArrayList<>();
        fieldAndStacks.stream()
                .forEach(l -> fieldAndStackList.add(l.toEntity()));


        Profile newProfile = Profile.builder()
                .refLinks(refLinks)
                .selfIntro(selfIntro)
                .career(career)
                .build();

        newProfile.setFieldAndStacks(fieldAndStackList);
        return newProfile;
    }
}
