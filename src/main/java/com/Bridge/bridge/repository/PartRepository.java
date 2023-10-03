package com.Bridge.bridge.repository;

import com.Bridge.bridge.domain.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PartRepository extends JpaRepository<Part, Long> {

    List<Part> findAllByRecruitSkillInAndAndRecruitPart(List<String> skills, String part);

    List<Part> findAllByRecruitPart(String part);
}
