package com.Bridge.bridge.exception.badrequest;

import com.Bridge.bridge.exception.BridgeException;
import org.springframework.http.HttpStatus;

public class FileUploadException extends BridgeException {

    public FileUploadException() {
        super(HttpStatus.BAD_REQUEST, "파일 업로드에 실패하였습니다.", 400);
    }
}
