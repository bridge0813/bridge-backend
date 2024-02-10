package com.Bridge.bridge.repository;

import com.Bridge.bridge.domain.SearchWord;
import com.Bridge.bridge.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchWordRepository extends JpaRepository<SearchWord, Long> {

    List<SearchWord> findAllByUserOrderByHistoryDesc(User user);

    void deleteAllByUser(User user);
}
