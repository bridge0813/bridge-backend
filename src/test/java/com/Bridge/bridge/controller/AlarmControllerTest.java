package com.Bridge.bridge.controller;

import com.Bridge.bridge.domain.Alarm;
import com.Bridge.bridge.domain.Platform;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.repository.AlarmRepository;
import com.Bridge.bridge.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AlarmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlarmRepository alarmRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
        alarmRepository.deleteAll();
    }

    @Test
    @DisplayName("전체 알람 조회")
    void getAllOfAlarms() throws Exception {
        // given
        User user = new User("user", "user@gmail.com", Platform.APPLE, "Test");
        User newUser = userRepository.save(user);

        Alarm alarm1 = Alarm.builder()
                .type("Applier")
                .title("지원자 등장?")
                .content("내 프로젝트에 누군가 지원했어요 지원자 프로필을 확인하고 채팅을 시작해보세요!")
                .rcvUser(newUser)
                .build();
        Alarm alarm2 = Alarm.builder()
                .type("Applier")
                .title("지원자 등장?")
                .content("내 프로젝트에 누군가 지원했어요 지원자 프로필을 확인하고 채팅을 시작해보세요!")
                .rcvUser(newUser)
                .build();
        Alarm alarm3 = Alarm.builder()
                .type("Apply")
                .title("지원 결과 도착")
                .content("내가 지원한 프로젝트의 결과가 나왔어요. 관리 페이지에서 확인해보세요.")
                .rcvUser(newUser)
                .build();

        alarmRepository.save(alarm1);
        alarmRepository.save(alarm2);
        alarmRepository.save(alarm3);

        // expected
        mockMvc.perform(get("/alarms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", user.getId().toString()))
                .andExpect(status().isOk()) // 응답 status를 ok로 테스트
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("지원자 등장?"))
                .andExpect(jsonPath("$[1].title").value("지원자 등장?"))
                .andExpect(jsonPath("$[2].title").value("지원 결과 도착"))
                .andDo(print());
    }

    @Test
    @DisplayName("전체 알람 삭제")
    void deleteAllOfAlarms() throws Exception {
        // given
        User user = new User("user", "user@gmail.com", Platform.APPLE, "Test");
        User newUser = userRepository.save(user);

        Alarm alarm1 = Alarm.builder()
                .type("Applier")
                .title("지원자 등장?")
                .content("내 프로젝트에 누군가 지원했어요 지원자 프로필을 확인하고 채팅을 시작해보세요!")
                .rcvUser(user)
                .build();
        Alarm alarm2 = Alarm.builder()
                .type("Applier")
                .title("지원자 등장?")
                .content("내 프로젝트에 누군가 지원했어요 지원자 프로필을 확인하고 채팅을 시작해보세요!")
                .rcvUser(user)
                .build();
        Alarm alarm3 = Alarm.builder()
                .type("Apply")
                .title("지원 결과 도착")
                .content("내가 지원한 프로젝트의 결과가 나왔어요. 관리 페이지에서 확인해보세요.")
                .rcvUser(user)
                .build();

        alarmRepository.save(alarm1);
        alarmRepository.save(alarm2);
        alarmRepository.save(alarm3);

        // expected
        mockMvc.perform(delete("/alarms")
                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(user.getId()))
                        .param("userId", user.getId().toString())
                )
                .andExpect(status().isOk()) // 응답 status를 ok로 테스트
                .andDo(print());
    }

    @Test
    @DisplayName("개별 알람 삭제")
    void deleteAlarm() throws Exception {
        // given
        User user = new User("user", "user@gmail.com", Platform.APPLE, "Test");
        userRepository.save(user);

        Alarm alarm1 = Alarm.builder()
                .type("Applier")
                .title("지원자 등장? - 1")
                .content("내 프로젝트에 누군가 지원했어요 지원자 프로필을 확인하고 채팅을 시작해보세요!")
                .sendDateTime(LocalDateTime.now())
                .rcvUser(user)
                .build();
        Alarm alarm2 = Alarm.builder()
                .type("Applier")
                .title("지원자 등장? - 2")
                .content("내 프로젝트에 누군가 지원했어요 지원자 프로필을 확인하고 채팅을 시작해보세요!")
                .sendDateTime(LocalDateTime.now())
                .rcvUser(user)
                .build();
        Alarm alarm3 = Alarm.builder()
                .type("Apply")
                .title("지원 결과 도착")
                .content("내가 지원한 프로젝트의 결과가 나왔어요. 관리 페이지에서 확인해보세요.")
                .sendDateTime(LocalDateTime.now())
                .rcvUser(user)
                .build();

        alarmRepository.save(alarm1);
        alarmRepository.save(alarm2);
        alarmRepository.save(alarm3);

        user.getRcvAlarms().add(alarm1);
        user.getRcvAlarms().add(alarm2);
        user.getRcvAlarms().add(alarm3);

        // expected
        mockMvc.perform(delete("/alarm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", user.getId().toString())
                        .content(objectMapper.writeValueAsString(alarm2.getId())))
                .andExpect(status().isOk()) // 응답 status를 ok로 테스트
                .andExpect(jsonPath("$[0].title").value("지원자 등장? - 1"))
                .andExpect(jsonPath("$[1].title").value("지원 결과 도착"))
                .andDo(print());
    }


}
