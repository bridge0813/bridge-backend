package com.Bridge.bridge.dto.response;

import com.Bridge.bridge.domain.ApplyProject;
import com.Bridge.bridge.domain.Project;
import com.Bridge.bridge.domain.User;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ApplyUserResponse {

    private Long userId;

    private String name;

    private List<String> fields; //관심 분야

    private String career;  //경력 사항

    @Builder
    private ApplyUserResponse(Long userId, String name, List<String> fields, String career) {
        this.userId = userId;
        this.name = name;
        this.fields = fields;
        this.career = career;
    }

    // 지원한 프로젝트를 통한 응답 DTO 생성
    public static ApplyUserResponse from(ApplyProject project) {
        User applyUser = project.getUser();

        List<String> fields = applyUser.getFields().stream()
                .map(f -> f.getValue())
                .collect(Collectors.toList());

        String career = null;
        if(applyUser.getProfile() != null && applyUser.getProfile().getCareer() != null) {
            career = applyUser.getProfile().getCareer();
        }

        return ApplyUserResponse.builder()
                .userId(applyUser.getId())
                .name(applyUser.getName())
                .fields(fields)
                .career(career).build();
    }
}
