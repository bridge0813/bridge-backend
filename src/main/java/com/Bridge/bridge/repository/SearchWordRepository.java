package com.Bridge.bridge.repository;

import com.Bridge.bridge.domain.SearchWord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchWordRepository extends JpaRepository<SearchWord, Long> {
}
