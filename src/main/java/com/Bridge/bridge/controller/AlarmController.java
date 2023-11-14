package com.Bridge.bridge.controller;

import com.Bridge.bridge.dto.response.AlarmResponse;
import com.Bridge.bridge.dto.response.AllAlarmResponse;
import com.Bridge.bridge.dto.response.ErrorResponse;
import com.Bridge.bridge.service.AlarmService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @Operation(summary = "디바이스 토큰 설정", description = "개별 기기마다 존재하는 고유 디바이스 토큰을 저장한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "디바이스 토큰 저장 완료"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (Unauthorized)")
    })
    @PostMapping("/device/token")
    public void saveDeviceToken(@RequestBody String deviceToken){
        alarmService.saveDeviceToken(deviceToken);
        return;
    }

    @Operation(summary = "전체 알람 조회 기능", description = "유저가 받은 모든 알람 조회할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 알람 조회 완료"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (Unauthorized)"),
            @ApiResponse(responseCode = "404", description = "전체 알람 조회 실패")
    })
    @GetMapping("/alarms")
    public List<AllAlarmResponse> getAllOfAlarms(@RequestParam Long userId){
        return alarmService.getAllOfAlarms(userId);
    }

    @Operation(summary = "전체 알람 삭제 기능", description = "유저가 받은 모든 알람을 삭제할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 알람 삭제 완료"),
            @ApiResponse(responseCode = "400", description = "전체 알람 삭제 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (Unauthorized)"),
            @ApiResponse(responseCode = "404", description = "유저 찾기 실패")
    })
    @DeleteMapping("/alarms")
    public ResponseEntity<?> deleteAllOfAlarms(@RequestParam Long userId){
        boolean result = alarmService.deleteAllAlarms(userId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "개별 알람 삭제 기능", description = "유저가 받은 알람들 중 하나의 알람을 삭제할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "개별 알람 삭제 완료"),
            @ApiResponse(responseCode = "400", description = "개별 알람 삭제 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (Unauthorized)"),
            @ApiResponse(responseCode = "404", description = "유저 찾기 실패 OR 해당 알람 찾기 실패")
    })
    @DeleteMapping("/alarm")
    public List<AlarmResponse> deleteAlarm(@RequestParam Long userId, @RequestBody Long alarmId){
        return alarmService.deleteAlarm(userId, alarmId);
    }
}
