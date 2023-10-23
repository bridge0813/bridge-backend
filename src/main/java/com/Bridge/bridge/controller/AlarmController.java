package com.Bridge.bridge.controller;

import com.Bridge.bridge.dto.response.AllAlarmResponse;
import com.Bridge.bridge.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @PostMapping("/device/token")
    public void saveDeviceToken(@RequestBody String deviceToken){
        alarmService.saveDeviceToken(deviceToken);
        return;
    }

    @PostMapping("/alarms")
    public List<AllAlarmResponse> getAllOfAlarms(@RequestBody Long userId){
        return alarmService.getAllOfAlarms(userId);
    }

    @DeleteMapping("/alarms")
    public ResponseEntity<?> deleteAllOfAlarms(@RequestBody Long userId){
        boolean result = alarmService.deleteAllAlarms(userId);
        return ResponseEntity.ok(result);
    }
}
