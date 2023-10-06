package com.Bridge.bridge.controller;

import com.Bridge.bridge.dto.response.ErrorResponse;
import com.Bridge.bridge.exception.BridgeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(BridgeException.class)
    public ResponseEntity<ErrorResponse> handleBridgeException(BridgeException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(new ErrorResponse(e.getCode(), e.getMessage()));
    }
}
