package com.Bridge.bridge.repository;

import com.Bridge.bridge.domain.Platform;

import java.util.Optional;

public interface UserRepositoryCustom {

    Optional<Long> findIdByPlatformAndPlatformId(Platform Platform, String PlatformId);
}
