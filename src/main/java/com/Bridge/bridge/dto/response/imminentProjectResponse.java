package com.Bridge.bridge.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class imminentProjectResponse {

    int imminentRank; // 마감일이 빠른 순위

    String title; // 모집글 제목

    String dueDate; // 모집글 작성 날짜, 시간

    @Builder
    public imminentProjectResponse(int imminentRank, String title, String dueDate) {
        this.imminentRank = imminentRank;
        this.title = title;
        this.dueDate = dueDate;
    }
}
