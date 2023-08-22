package com.Bridge.bridge.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Project {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    private String title;           //제목

    private String subject;         //주제

    private String purpose;         //목적

    private String dueDate;         //기간

    private int recruitNum;         //모집인원 수

    private String recruitSkill;    //모집 분야

    private String tagLimit;        //지원자 태그 제한록

    private String meetingWay;      //대면 or 비대면 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;                                              // 해당 프로젝트 글을 만든 유저

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ApplyProject> applyProjects = new ArrayList<>();   // 해당 프로젝트 글에 지원한 유저 목록

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Bookmark> bookmarks = new ArrayList<>();           // 해당 프로젝트 글을 북마크한 유저 목록
}
