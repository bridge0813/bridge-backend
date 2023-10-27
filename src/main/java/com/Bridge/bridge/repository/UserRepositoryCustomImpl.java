package com.Bridge.bridge.repository;

import com.Bridge.bridge.domain.Platform;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import java.util.Optional;

import static com.Bridge.bridge.domain.QUser.user;


@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
   public Optional<Long> findIdByPlatformAndPlatformId(Platform platform, String platformId) {
        Long userId = jpaQueryFactory.
                select(user.id)
                .from(user)
                .where(user.platform.eq(platform), user.platformId.eq(platformId))
                .fetchOne();

        return Optional.ofNullable(userId);
    }
}
