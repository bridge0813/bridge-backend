package com.Bridge.bridge.domain;

import com.Bridge.bridge.dto.request.ProfileUpdateRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
public class Profile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    private String selfIntro;       // 자기소개서

    private String career;          // 경력 사항

    @ElementCollection
    private List<String> refLinks = new ArrayList<>();         // 참고 링크

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FieldAndStack> fieldAndStacks = new ArrayList<>();   // 기술 스택

    @OneToOne(mappedBy = "profile")
    private User user;              // 해당 프로필을 작성한 유저

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "file_id", name = "file_photo_id")
    private File profilePhoto;      // 프로필 사진

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<File> refFiles = new ArrayList<>();           // 첨부 파일

    @Builder
    public Profile(List<String> refLinks, String selfIntro, String career) {
        this.refLinks = refLinks;
        this.selfIntro = selfIntro;
        this.career = career;
    }

    public void updateUser(User user) {
        this.user = user;
    }

    public List<Long> updateProfile(ProfileUpdateRequest request) {
        this.selfIntro = request.getSelfIntro();
        this.career = request.getCareer();

        //필드 + 스택 업데이트
        this.fieldAndStacks.clear();
        List<FieldAndStack> newFieldAndStacks = request.getFieldAndStacks().stream()
                .map(l -> l.toEntity())
                .collect(Collectors.toList());

        this.fieldAndStacks.addAll(newFieldAndStacks);
        newFieldAndStacks.stream()
                .forEach(l -> l.setProfile(this));

        this.refLinks = request.getRefLinks() == null ? new ArrayList<>() : request.getRefLinks();

        //파일 업데이트
        if (request.getFileIds() != null && !request.getFileIds().isEmpty()) {
            List<Long> oldFileIds = this.getRefFiles().stream()
                    .map(f -> f.getId())
                    .collect(Collectors.toList());
            System.out.println("oldFileIds" + oldFileIds.size());
            System.out.println("want id" + request.getFileIds());

            oldFileIds.removeAll(request.getFileIds());

            System.out.println("oldFileIds" + oldFileIds.size());
            return oldFileIds;
        }
        return null;
    }

    // --연관관계 메소드 -- //
    public File setPhotoFile(File file) {
        // 프로필 자운 후 업데이트
        if(Objects.nonNull(this.profilePhoto)) {
           File oldPhoto = this.profilePhoto;
           this.profilePhoto = file;
           file.setProfilePhoto(this);
           return oldPhoto;
        }
        this.profilePhoto = file;
        file.setProfilePhoto(this);
        return null;
    }

    public void setRefFiles(List<File> files) {
        this.refFiles.addAll(files);
        files.stream().forEach(f -> f.setProfile(this));
    }

    public void setFieldAndStacks(List<FieldAndStack> fieldAndStacks) {
        this.fieldAndStacks = fieldAndStacks;
        fieldAndStacks.stream()
                .forEach(l -> l.setProfile(this));
    }
}
