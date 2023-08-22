package com.Bridge.bridge.domain;

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

    private String fileName;            // 고유 경로 -> 만약 저장한 파일의 이름이 같은경우 중복되기 때문에 고유 경로가 필요

    private String originName;          // 파일 원래 이름

    private String ext;                 // 파일 확장자

    private Long fileSize;              // 파일 사이즈

    @OneToOne(mappedBy = "file")
    private Message message;            // 해당 파일이 저장된 메세지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;            // 해당 파일이 저장된 프로필



}
