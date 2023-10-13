package com.Bridge.bridge.repository;

import com.Bridge.bridge.domain.ApplyProject;
import com.Bridge.bridge.domain.Project;
import com.Bridge.bridge.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplyProjectRepository extends JpaRepository<ApplyProject, Long> {
    Optional<ApplyProject> findByUserAndProject(User user, Project project);
    void deleteByUserAndProject(User user, Project project);
}
