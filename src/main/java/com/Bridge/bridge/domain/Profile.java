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
public class Profile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    private String refLink;         // 참고 링크

    private String selfIntro;       // 자기소개서

    private String career;          // 경력 사항

    @ElementCollection
    private List<String> skill;           // 기술 스택

    @OneToOne(mappedBy = "profile")
    private User user;              // 해당 프로필을 작성한 유저

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "file_id", name = "file_photo_id")
    private File profilePhoto;      // 프로필 사진

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "file_id", name = "file_ref_id")
    private File refFile;           // 첨부 파일

    @Builder
    public Profile(String refLink, String selfIntro, String career, List<String> skill) {
        this.refLink = refLink;
        this.selfIntro = selfIntro;
        this.career = career;
        this.skill = skill;
    }

    public void updateUser(User user) {
        this.user = user;
    }

    // --연관관계 메소드 -- //
    public File setProfilePhoto(File file) {
        // 프로필 자운 후 덥데이트
        if(Objects.nonNull(this.profilePhoto)) {
           File photo = this.profilePhoto;
           this.profilePhoto = file;
           return photo;
        }
        this.profilePhoto = file;
        file.setProfilePhoto(this);
        return null;
    }

    public File setRefFile(File file) {
        // 프로필 자운 후 덥데이트
        if(Objects.nonNull(this.refFile)) {
            File photo = this.refFile;
            this.refFile = file;
            return photo;
        }
        this.refFile = file;
        file.setProfileRef(this);
        return null;
    }
}
