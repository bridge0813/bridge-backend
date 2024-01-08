package com.Bridge.bridge.dto.response;

import com.Bridge.bridge.domain.User;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class MyPageResponse {

    private String name;

    private String profilePhoto;

    private List<String> field;

    private int bookmarkNum;

    @Builder
    private MyPageResponse(String name, String profilePhoto, List<String> field, int bookmarkNum) {
        this.name = name;
        this.profilePhoto = profilePhoto;
        this.field = field;
        this.bookmarkNum = bookmarkNum;
    }

    public static MyPageResponse NoProfilePhoto(User user) {
        return MyPageResponse.builder()
                .name(user.getName())
                .profilePhoto(null)
                .field(user.getFields().stream()
                        .map(f -> f.getValue())
                        .collect(Collectors.toList()))
                .bookmarkNum(user.getBookmarks().size())
                .build();
    }

    public static MyPageResponse YesProfilePhoto(User user, String photo) {
        return MyPageResponse.builder()
                .name(user.getName())
                .profilePhoto(user.getProfile().getProfilePhoto().getUploadFileUrl())
                .field(user.getFields().stream()
                        .map(f -> f.getValue())
                        .collect(Collectors.toList()))
                .bookmarkNum(user.getBookmarks().size())
                .build();
    }
}
