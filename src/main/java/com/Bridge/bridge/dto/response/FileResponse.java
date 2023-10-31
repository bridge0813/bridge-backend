package com.Bridge.bridge.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FileResponse {

    private String url;             // 저장 경로

    private String originFileName;  //원본 파일 명

    @Builder
    public FileResponse(String url, String originFileName) {
        this.url = url;
        this.originFileName = originFileName;
    }
}
