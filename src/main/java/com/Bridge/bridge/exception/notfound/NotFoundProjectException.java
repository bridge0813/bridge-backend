package com.Bridge.bridge.exception.notfound;

import com.Bridge.bridge.exception.BridgeException;
import org.springframework.http.HttpStatus;

public class NotFoundProjectException extends BridgeException {
    public NotFoundProjectException() {
        super(HttpStatus.NOT_FOUND, "존재하지 않는 프로젝트입니다.", 404);
    }
}
