package com.Bridge.bridge.exception.badrequest;

import com.Bridge.bridge.exception.BridgeException;
import org.springframework.http.HttpStatus;

public class AlarmDeleteException extends BridgeException {

    public AlarmDeleteException() {
        super(HttpStatus.BAD_REQUEST, "전체 알람이 삭제되지 않았습니다.", 400);
    }
}
