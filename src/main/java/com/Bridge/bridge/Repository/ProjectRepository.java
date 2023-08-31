package com.Bridge.bridge.Repository;

import com.Bridge.bridge.domain.Project;
import com.Bridge.bridge.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Override
    List<Project> findAll();

    @Override
    <S extends Project> S save(S entity);
}
