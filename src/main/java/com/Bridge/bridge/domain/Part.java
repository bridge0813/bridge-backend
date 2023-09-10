package com.Bridge.bridge.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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
    private List<String> recruitSkill;    //모집 기술 스택

    private String requirement; // 모집 요건

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder
    public Part(String recruitPart, int recruitNum, List<String> recruitSkill, String requirement, Project project) {
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
}
