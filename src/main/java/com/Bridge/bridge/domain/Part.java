package com.Bridge.bridge.domain;

import com.Bridge.bridge.dto.response.PartResponseDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_id")
    private Long id;

    private String recruitPart;     // 모집 파트

    private int recruitNum;         //모집인원 수

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private List<Stack> recruitSkill = new ArrayList<>();    //모집 기술 스택

    private String requirement; // 모집 요건

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder
    public Part(String recruitPart, int recruitNum, List<Stack> recruitSkill, String requirement, Project project) {
        this.recruitPart = recruitPart;
        this.recruitNum = recruitNum;
        this.recruitSkill = recruitSkill;
        this.requirement = requirement;
        this.project = project;
    }

    public void setProject(Project project) {
        this.project = project;
        project.getRecruit().add(this);
    }

    public PartResponseDto toDto(){
        return PartResponseDto.builder()
                .recruitPart(this.getRecruitPart())
                .recruitNum(this.getRecruitNum())
                .recruitSkill(this.getRecruitSkill())
                .requirement(this.getRequirement())
                .build();
    }



}
