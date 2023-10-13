package com.Bridge.bridge.exception.notfound;

import com.Bridge.bridge.exception.BridgeException;
import org.springframework.http.HttpStatus;

public class NotFoundSearchWordException extends BridgeException {

    public NotFoundSearchWordException() {
        super(HttpStatus.NOT_FOUND, "존재하지 않는 검색어입니다.", 404);
    }
}
