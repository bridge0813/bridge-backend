package com.Bridge.bridge.exception.notfound;

import com.Bridge.bridge.exception.BridgeException;
import org.springframework.http.HttpStatus;

public class NotFoundProfileException extends BridgeException {

    public NotFoundProfileException() {
        super(HttpStatus.NOT_FOUND, "프로필을 등록해주세요!", 404);
    }

    public NotFoundProfileException(String message) {
        super(HttpStatus.NOT_FOUND, message, 404);
    }
}
