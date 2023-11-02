package com.Bridge.bridge.controller;

import com.Bridge.bridge.dto.response.AlarmResponse;
import com.Bridge.bridge.dto.response.AllAlarmResponse;
import com.Bridge.bridge.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/alarms")
    public List<AllAlarmResponse> getAllOfAlarms(@RequestParam Long userId){
        return alarmService.getAllOfAlarms(userId);
    }

    @DeleteMapping("/alarms")
    public ResponseEntity<?> deleteAllOfAlarms(@RequestParam Long userId){
        boolean result = alarmService.deleteAllAlarms(userId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/alarm")
    public List<AlarmResponse> deleteAlarm(@RequestParam Long userId, @RequestBody Long alarmId){
        return alarmService.deleteAlarm(userId, alarmId);
    }
}
