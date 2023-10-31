package com.Bridge.bridge.exception.notfound;

import com.Bridge.bridge.exception.BridgeException;
import org.springframework.http.HttpStatus;

public class NotFoundAlarmException extends BridgeException {

    public NotFoundAlarmException() {
        super(HttpStatus.NOT_FOUND, "알람이 존재하지 않습니다.", 404);
    }

}
