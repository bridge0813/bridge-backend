package com.Bridge.bridge.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MyPageResponse {

    private String profilePhoto;

    private List<String> field;

    private int bookmarkNum;

    @Builder
    public MyPageResponse(String profilePhoto, List<String> field, int bookmarkNum) {
        this.profilePhoto = profilePhoto;
        this.field = field;
        this.bookmarkNum = bookmarkNum;
    }
}
