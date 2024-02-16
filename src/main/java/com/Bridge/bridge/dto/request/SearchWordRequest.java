package com.Bridge.bridge.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchWordRequest {

    private long userId; // 검색한 유저의 아이디

    private String searchWord; // 검색어
}
