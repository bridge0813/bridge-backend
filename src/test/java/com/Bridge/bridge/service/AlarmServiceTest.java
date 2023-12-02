package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Alarm;
import com.Bridge.bridge.domain.Platform;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.response.AlarmResponse;
import com.Bridge.bridge.dto.response.AllAlarmResponse;
import com.Bridge.bridge.repository.AlarmRepository;
import com.Bridge.bridge.repository.UserRepository;
import com.Bridge.bridge.security.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class AlarmServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
        alarmRepository.deleteAll();
    }

    @DisplayName("모든 알람 조회")
    @Test
    void getAllAlarm() {
        // given
        User user = new User("user", "user@gmaiil.com", Platform.APPLE, "alarm");
        userRepository.save(user);

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

        MockHttpServletRequest request = new MockHttpServletRequest();

        String token = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        request.addHeader("Authorization", "Bearer " + token);

        // when
        List<AllAlarmResponse> responses = alarmService.getAllOfAlarms(request);

        // then
        Assertions.assertThat(responses.size()).isEqualTo(3);
        Assertions.assertThat(responses.get(0).getTitle()).isEqualTo("지원자 등장?");
        Assertions.assertThat(responses.get(1).getTitle()).isEqualTo("지원자 등장?");
        Assertions.assertThat(responses.get(2).getTitle()).isEqualTo("지원 결과 도착");
    }

    @DisplayName("모든 알람 삭제")
    @Test
    void deleteAllAlarm() {
        // given
        User user = new User("user", "user@gmaiil.com", Platform.APPLE, "alarm");
        userRepository.save(user);

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

        MockHttpServletRequest request = new MockHttpServletRequest();

        String token = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        request.addHeader("Authorization", "Bearer " + token);

        // when
        boolean responses = alarmService.deleteAllAlarms(request);

        // then
        Assertions.assertThat(responses).isEqualTo(true);
    }

    @DisplayName("모든 알람 삭제 - 해당 유저 것만 삭제되는지 확인")
    @Test
    void deleteAllAlarm_diffrentUser() {
        // given
        User user1 = new User("user1", "user1@gmaiil.com", Platform.APPLE, "alarm");
        User user2 = new User("user2", "user2@gmaiil.com", Platform.APPLE, "alarm");
        userRepository.save(user1);
        userRepository.save(user2);

        Alarm alarm1 = Alarm.builder()
                .type("Applier")
                .title("지원자 등장?")
                .content("내 프로젝트에 누군가 지원했어요 지원자 프로필을 확인하고 채팅을 시작해보세요!")
                .rcvUser(user1)
                .build();
        Alarm alarm2 = Alarm.builder()
                .type("Applier")
                .title("지원자 등장?")
                .content("내 프로젝트에 누군가 지원했어요 지원자 프로필을 확인하고 채팅을 시작해보세요!")
                .rcvUser(user1)
                .build();
        Alarm alarm3 = Alarm.builder()
                .type("Apply")
                .title("지원 결과 도착")
                .content("내가 지원한 프로젝트의 결과가 나왔어요. 관리 페이지에서 확인해보세요.")
                .rcvUser(user2)
                .build();

        alarmRepository.save(alarm1);
        alarmRepository.save(alarm2);
        alarmRepository.save(alarm3);

        MockHttpServletRequest request = new MockHttpServletRequest();

        String token = Jwts.builder()
                .setSubject(String.valueOf(user1.getId()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        request.addHeader("Authorization", "Bearer " + token);

        // when
        boolean responses = alarmService.deleteAllAlarms(request);
        int user2Alarms = alarmRepository.findAllByRcvUser(user2).size();

        // then
        Assertions.assertThat(responses).isEqualTo(true);
        Assertions.assertThat(user2Alarms).isEqualTo(1);
    }

    @DisplayName("개별 알람 삭제")
    @Test
    void deleteAlarm() {
        // given
        User user1 = new User("user1", "user1@gmaiil.com", Platform.APPLE, "alarm");
        userRepository.save(user1);

        Alarm alarm1 = Alarm.builder()
                .type("Applier")
                .title("지원자 등장? - 1")
                .content("내 프로젝트에 누군가 지원했어요 지원자 프로필을 확인하고 채팅을 시작해보세요!")
                .sendDateTime(LocalDateTime.now())
                .rcvUser(user1)
                .build();
        Alarm alarm2 = Alarm.builder()
                .type("Applier")
                .title("지원자 등장? - 2")
                .content("내 프로젝트에 누군가 지원했어요 지원자 프로필을 확인하고 채팅을 시작해보세요!")
                .sendDateTime(LocalDateTime.now())
                .rcvUser(user1)
                .build();
        Alarm alarm3 = Alarm.builder()
                .type("Apply")
                .title("지원 결과 도착")
                .content("내가 지원한 프로젝트의 결과가 나왔어요. 관리 페이지에서 확인해보세요.")
                .sendDateTime(LocalDateTime.now())
                .rcvUser(user1)
                .build();

        alarmRepository.save(alarm1);
        alarmRepository.save(alarm2);
        alarmRepository.save(alarm3);

        user1.getRcvAlarms().add(alarm1);
        user1.getRcvAlarms().add(alarm2);
        user1.getRcvAlarms().add(alarm3);

        MockHttpServletRequest request = new MockHttpServletRequest();

        String token = Jwts.builder()
                .setSubject(String.valueOf(user1.getId()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        request.addHeader("Authorization", "Bearer " + token);

        // when
        List<AlarmResponse> responses = alarmService.deleteAlarm(request, alarm1.getId());

        // then

        Assertions.assertThat(responses.size()).isEqualTo(2);
        Assertions.assertThat(responses.get(0).getTitle()).isEqualTo("지원자 등장? - 2");
        Assertions.assertThat(responses.get(1).getTitle()).isEqualTo("지원 결과 도착");

    }



}
