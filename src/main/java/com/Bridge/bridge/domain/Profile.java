package com.Bridge.bridge.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Profile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    private String skill;           // 기술 스택

    private String refLink;         // 참고 링크

    private String refFile;         // 첨부 파일

    private String selfIntro;       // 자기소개서

    private String career;          // 경력 사항

    @OneToOne(mappedBy = "profile")
    private User user;              // 해당 프로필을 작성한 유저

    @OneToMany(mappedBy = "profile")
    private List<File> files = new ArrayList<>();
}
