package com.Bridge.bridge.exception.badrequest;

import com.Bridge.bridge.exception.BridgeException;
import org.springframework.http.HttpStatus;

public class FileDeleteException extends BridgeException {

    public FileDeleteException() {
        super(HttpStatus.BAD_REQUEST, "파일 삭제에 실패했습니다.", 400);
    }

    public FileDeleteException(String message) {
        super(HttpStatus.BAD_REQUEST, message, 400);
    }
}
