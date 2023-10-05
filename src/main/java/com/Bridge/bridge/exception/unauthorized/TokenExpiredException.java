package com.Bridge.bridge.exception.unauthorized;

import com.Bridge.bridge.exception.BridgeException;
import org.springframework.http.HttpStatus;

public class TokenExpiredException extends BridgeException {

    public TokenExpiredException() {
        super(HttpStatus.UNAUTHORIZED, "로그인 인증 유효기간이 만료되었습니다. 다시 로그인 해주세요.", 401);
    }

    public TokenExpiredException(String message) {
        super(HttpStatus.UNAUTHORIZED, message, 401);
    }
}
