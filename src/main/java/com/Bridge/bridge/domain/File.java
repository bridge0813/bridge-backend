package com.Bridge.bridge.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    private String uploadFileUrl;            // 저장 경로

    private String keyName;                 //  업로드 시 키

    private String originName;          // 파일 원래 이름

    private Long fileSize;              // 파일 사이즈

    @OneToOne(mappedBy = "profilePhoto")
    private Profile profilePhoto;            // 해당 파일이 저장된 프로필

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;            // 해당 파일이 저장된 프로필

    @Builder
    public File(String uploadFileUrl, String keyName, String originName, Long fileSize) {
        this.uploadFileUrl = uploadFileUrl;
        this.keyName = keyName;
        this.originName = originName;
        this.fileSize = fileSize;
    }

    public void setProfilePhoto(Profile profile) {
        this.profilePhoto = profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
