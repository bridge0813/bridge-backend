package com.Bridge.bridge.exception.unauthorized;

import com.Bridge.bridge.exception.BridgeException;
import org.springframework.http.HttpStatus;

public class InvalidBearerException extends BridgeException {
    public InvalidBearerException() {
        super(HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스입니다.", 401);
    }

    public InvalidBearerException(String message) {
        super(HttpStatus.UNAUTHORIZED, message , 401);
    }

}
