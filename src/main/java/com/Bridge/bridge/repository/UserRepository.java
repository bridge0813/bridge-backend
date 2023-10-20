package com.Bridge.bridge.repository;

import com.Bridge.bridge.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findByRefreshToken(String refreshToken);

    User findByEmail(String email);

    User findByDeviceToken(String deviceToken);
<<<<<<< HEAD
<<<<<<< HEAD

    User findByName(String name);
=======
>>>>>>> c4e2fb2 (FEAT : 앱 실행 시 DeviceToken 저장하기 기능 구현)
=======

    User findByName(String name);
>>>>>>> 782435c (FEAT : 채팅 수신 시 알림 보내기 기능 구현)
}
