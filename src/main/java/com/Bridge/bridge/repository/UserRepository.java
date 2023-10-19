package com.Bridge.bridge.repository;

import com.Bridge.bridge.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findByRefreshToken(String refreshToken);

    User findByEmail(String email);

    User findByDeviceToken(String deviceToken);
}
