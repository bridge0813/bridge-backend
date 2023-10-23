package com.Bridge.bridge.repository;

import com.Bridge.bridge.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {


}
