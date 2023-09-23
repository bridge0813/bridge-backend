package com.Bridge.bridge.exception.unauthorized;

import com.Bridge.bridge.exception.BridgeException;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends BridgeException {

    public InvalidTokenException() {
        super(HttpStatus.UNAUTHORIZED, "올바르지 않은 형식의 토큰입니다. 다시 로그인해주세요.", 401);
    }

    public InvalidTokenException(String message) {
        super(HttpStatus.UNAUTHORIZED, message , 401);
    }
}
