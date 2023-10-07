package com.Bridge.bridge.repository;

import com.Bridge.bridge.domain.Bookmark;
import com.Bridge.bridge.domain.Project;
import com.Bridge.bridge.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Bookmark findByProjectAndUser(Project project, User user);
}
