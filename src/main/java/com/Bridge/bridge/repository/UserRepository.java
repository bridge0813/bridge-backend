package com.Bridge.bridge.repository;

import com.Bridge.bridge.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findByRefreshToken(String refreshToken);

    User findByDeviceToken(String deviceToken);

    User findByName(String name);

}
