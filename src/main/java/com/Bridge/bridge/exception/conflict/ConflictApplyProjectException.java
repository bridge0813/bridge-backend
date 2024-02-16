package com.Bridge.bridge.exception.conflict;

import com.Bridge.bridge.exception.BridgeException;
import org.springframework.http.HttpStatus;

public class ConflictApplyProjectException extends BridgeException {
    public ConflictApplyProjectException() {
        super(HttpStatus.CONFLICT, "이미 지원한 프로젝트입니다.", 409);
    }
}
