package com.Bridge.bridge.repository;

import com.Bridge.bridge.domain.Part;
import com.Bridge.bridge.domain.Project;
import com.Bridge.bridge.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Override
    List<Project> findAll();

    //Optional<Project> findByUser_Id(Long UserId);

    @Override
    <S extends Project> S save(S entity);

    List<Project> findAllByRecruitInAndDueDateGreaterThanEqual(List<Part> parts, String dueDate);

    List<Project> findAllByUser(User user);

    List<Project> findAllByDueDateGreaterThanEqualOrderByUploadTime(String dueDate);

    List<Project> findTop20ByDueDateGreaterThanEqualOrderByBookmarkNumDesc(String dueDate);

    List<Project> findTop40ByDueDateGreaterThanEqualOrderByDueDate(LocalDateTime dueDate);
}
