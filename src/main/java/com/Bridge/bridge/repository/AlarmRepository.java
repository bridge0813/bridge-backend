package com.Bridge.bridge.repository;

import com.Bridge.bridge.domain.Alarm;
import com.Bridge.bridge.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    List<Alarm> findAllByRcvUser(User user);
}
