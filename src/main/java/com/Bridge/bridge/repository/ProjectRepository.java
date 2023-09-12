package com.Bridge.bridge.repository;

import com.Bridge.bridge.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Override
    List<Project> findAll();

    Optional<Project> findByUser_Id(Long UserId);

    @Override
    <S extends Project> S save(S entity);
}
