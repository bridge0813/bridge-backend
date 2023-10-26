package com.Bridge.bridge.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String name;                // 이름

    private String email;               // 이메일

    private String platformId;          // 소셜 로그인 고유 아이디

    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private Platform platform;          // 플랫폼 구별 enum

    //@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(value = EnumType.STRING)
    private List<Field> fields = new ArrayList<>(); // 관심 분야

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private Profile profile;            // 개인 프로필

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Project> projects = new ArrayList<>();             // 내가 만든 프로젝트 모집 글 목록

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ApplyProject> applyProjects = new ArrayList<>();   // 내가 지원한 프로젝트 목록

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Bookmark> bookmarks = new ArrayList<>();           // 내가 북마크한 프로젝트 글 목록

    @OneToMany(mappedBy = "makeUser", cascade = CascadeType.ALL)
    private List<Chat> madeChat = new ArrayList<>();           // 내가 만든 채팅방 목록

    @OneToMany(mappedBy = "receiveUser", cascade = CascadeType.ALL)
    private List<Chat> joinChat = new ArrayList<>();           // 내가 참여한 채팅방 목록

    @OneToMany(mappedBy = "rcvUser", cascade = CascadeType.ALL)
    private List<Alarm> rcvAlarms = new ArrayList<>();              // 알림 수신 목록

    @OneToMany(mappedBy = "sendUser", cascade = CascadeType.ALL)
    private List<Alarm> sendAlarms = new ArrayList<>();             // 알림 발신 목록

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<StaticMessage> staticMessages = new ArrayList<>();         // 유저가 만든 지정 메세지 목록

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<SearchWord> searchWords = new ArrayList<>();         // 유저가 검색한 검색어 목록

    public User(String name, String email, Platform platform, String platformId) {
        this.name = name;
        this.email = email;
        this.platform = platform;
        this.platformId = platformId;
    }

    public void setProject(Project project) {
        this.projects.add(project);
    }

    public void setBookmarks(Bookmark bookmark){
        this.bookmarks.add(bookmark);
    }
     

    //-- 연관관계 편의 메소드 --//
    public void updateProfile(Profile profile) {
        this.profile = profile;
        profile.updateUser(this);
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
