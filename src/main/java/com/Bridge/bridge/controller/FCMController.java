package com.Bridge.bridge.controller;

import com.Bridge.bridge.service.FCMService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FCMController {

    private final FCMService fcmService;

    @PostMapping("/device/token")
    public void saveDeviceToken(@RequestBody String deviceToken){
        fcmService.saveDeviceToken(deviceToken);
        return;
    }
}
