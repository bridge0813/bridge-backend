package com.Bridge.bridge.exception.notfound;

import com.Bridge.bridge.exception.BridgeException;
import org.springframework.http.HttpStatus;

public class NotFoundUserException extends BridgeException {

    public NotFoundUserException() {
        super(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다.", 404);
    }

    public NotFoundUserException(String message) {
        super(HttpStatus.NOT_FOUND, message, 404);
    }
}
