package com.Bridge.bridge.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class BookmarkResponse {

    private Long projectId;

    private Long userId;

    private String scrap;

    @Builder
    public BookmarkResponse(Long projectId, Long userId, String scrap) {
        this.projectId = projectId;
        this.userId = userId;
        this.scrap = scrap;
    }
}
