package com.Bridge.bridge.repository;

import com.Bridge.bridge.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
