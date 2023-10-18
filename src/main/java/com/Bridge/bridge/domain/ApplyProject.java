package com.Bridge.bridge.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
public class ApplyProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "joinProject_id")
    private Long id;

    private String stage;  // 지원 상태
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    public ApplyProject() {
        this.stage = "결과 대기중";
    }

    public void setUserAndProject(User user, Project project) {
        this.user = user;
        this.project = project;
    }

    public void changeStage(String stage) {
        this.stage = stage;
    }
}
