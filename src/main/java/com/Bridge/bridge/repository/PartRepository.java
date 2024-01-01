package com.Bridge.bridge.repository;

import com.Bridge.bridge.domain.Field;
import com.Bridge.bridge.domain.Part;
import com.Bridge.bridge.domain.Stack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PartRepository extends JpaRepository<Part, Long> {

    List<Part> findAllByRecruitSkillInAndAndRecruitPart(List<Stack> skills, Field part);

    List<Part> findAllByRecruitPart(Field part);
}
