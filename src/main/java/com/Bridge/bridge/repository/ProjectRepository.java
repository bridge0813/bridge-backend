package com.Bridge.bridge.repository;

import com.Bridge.bridge.domain.Part;
import com.Bridge.bridge.domain.Project;
import com.Bridge.bridge.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Override
    List<Project> findAll();

    //Optional<Project> findByUser_Id(Long UserId);

    @Override
    <S extends Project> S save(S entity);

    List<Project> findAllByRecruitInAndDueDateGreaterThanEqual(List<Part> parts, LocalDateTime dueDate);

    List<Project> findAllByUser(User user);

    @Query(value = "SELECT distinct p FROM Project AS p LEFT JOIN FETCH p.recruit WHERE p.dueDate >= :dueDate ORDER BY p.uploadTime")
    List<Project> findAllByDueDateGreaterThanEqualOrderByUploadTime(@Param("dueDate") LocalDateTime dueDate);
//    List<Project> findAllByDueDateGreaterThanEqualOrderByUploadTime(LocalDateTime dueDate);

    List<Project> findTop20ByDueDateGreaterThanEqualOrderByBookmarkNumDesc(LocalDateTime dueDate);

    List<Project> findTop40ByDueDateGreaterThanEqualOrderByDueDate(LocalDateTime dueDate);
}
