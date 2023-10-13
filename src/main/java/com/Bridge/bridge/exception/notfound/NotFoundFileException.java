package com.Bridge.bridge.exception.notfound;

import com.Bridge.bridge.exception.BridgeException;
import org.springframework.http.HttpStatus;

public class NotFoundFileException extends BridgeException {

    public NotFoundFileException() {
        super(HttpStatus.NOT_FOUND, "존재하지 않는 파일입니다.", 404);
    }

    public NotFoundFileException(String message) {
        super(HttpStatus.NOT_FOUND, message, 404);
    }
}
