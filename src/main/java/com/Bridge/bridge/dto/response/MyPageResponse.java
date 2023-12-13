package com.Bridge.bridge.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MyPageResponse {

    private String name;

    private String profilePhoto;

    private List<String> field;

    private int bookmarkNum;

    @Builder
    public MyPageResponse(String name, String profilePhoto, List<String> field, int bookmarkNum) {
        this.name = name;
        this.profilePhoto = profilePhoto;
        this.field = field;
        this.bookmarkNum = bookmarkNum;
    }
}
