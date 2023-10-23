package com.Bridge.bridge.controller;

import com.Bridge.bridge.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @PostMapping("/device/token")
    public void saveDeviceToken(@RequestBody String deviceToken){
        alarmService.saveDeviceToken(deviceToken);
        return;
    }
}
