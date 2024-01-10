package com.Bridge.bridge.dto.response;

import com.Bridge.bridge.domain.File;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FileResponse {

    private Long fileId;

    private String url;             // 저장 경로

    private String originFileName;  //원본 파일 명

    @Builder
    private FileResponse(Long fileId, String url, String originFileName) {
        this.fileId = fileId;
        this.url = url;
        this.originFileName = originFileName;
    }

    public static FileResponse from(File file) {
        return FileResponse.builder()
                .fileId(file.getId())
                .url(file.getUploadFileUrl())
                .originFileName(file.getOriginName()).build();
    }
}
