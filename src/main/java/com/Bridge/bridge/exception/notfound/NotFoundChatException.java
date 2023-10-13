package com.Bridge.bridge.exception.notfound;

import com.Bridge.bridge.exception.BridgeException;
import org.springframework.http.HttpStatus;

public class NotFoundChatException extends BridgeException {

    public NotFoundChatException() {
        super(HttpStatus.NOT_FOUND, "존재하지 않는 채팅방입니다.", 404);
    }
}
