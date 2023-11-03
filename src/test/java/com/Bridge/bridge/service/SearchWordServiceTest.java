package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Platform;
import com.Bridge.bridge.domain.SearchWord;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.response.SearchWordResponseDto;
import com.Bridge.bridge.repository.SearchWordRepository;
import com.Bridge.bridge.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class SearchWordServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SearchWordRepository searchWordRepository;

    @Autowired
    private SearchWordService searchWordService;

    @DisplayName("최근 검색어 조회")
    @Test
    void resentSearchWord() {
        // given
        User user = new User("user1", "user1@gmail.com", Platform.APPLE, "Test");
        user = userRepository.save(user);

        SearchWord newSearch1 = SearchWord.builder()
                .content("검색어1")
                .user(user)
                .history(LocalDateTime.now())
                .build();
        SearchWord newSearch2 = SearchWord.builder()
                .content("검색어2")
                .user(user)
                .history(LocalDateTime.now())
                .build();
        SearchWord newSearch3 = SearchWord.builder()
                .content("검색어3")
                .user(user)
                .history(LocalDateTime.now())
                .build();

        searchWordRepository.save(newSearch1);
        searchWordRepository.save(newSearch2);
        searchWordRepository.save(newSearch3);

        user.getSearchWords().add(newSearch1);
        user.getSearchWords().add(newSearch2);
        user.getSearchWords().add(newSearch3);

        // when
        List<SearchWordResponseDto> searchWordResponseDto = searchWordService.resentSearchWord(user.getId());
        Assertions.assertThat(searchWordResponseDto.get(0).getSearchWord()).isEqualTo("검색어1");
        Assertions.assertThat(searchWordResponseDto.get(1).getSearchWord()).isEqualTo("검색어2");
        Assertions.assertThat(searchWordResponseDto.get(2).getSearchWord()).isEqualTo("검색어3");
    }

    @DisplayName("최근 검색어 삭제")
    @Test
    void deleteSearchWord() {
        // given
        User user = new User("user1", "user1@gmail.com", Platform.APPLE, "Test");
        user = userRepository.save(user);

        SearchWord newSearch1 = SearchWord.builder()
                .content("검색어1")
                .user(user)
                .history(LocalDateTime.now())
                .build();
        SearchWord newSearch2 = SearchWord.builder()
                .content("검색어2")
                .user(user)
                .history(LocalDateTime.now())
                .build();
        SearchWord newSearch3 = SearchWord.builder()
                .content("검색어3")
                .user(user)
                .history(LocalDateTime.now())
                .build();

        searchWordRepository.save(newSearch1);
        searchWordRepository.save(newSearch2);
        searchWordRepository.save(newSearch3);

        user.getSearchWords().add(newSearch1);
        user.getSearchWords().add(newSearch2);
        user.getSearchWords().add(newSearch3);

        // when
        List<SearchWordResponseDto> searchWordResponseDto = searchWordService.deleteSearchWord(user.getId(), newSearch1.getId());
        Assertions.assertThat(searchWordResponseDto.get(0).getSearchWord()).isEqualTo("검색어2");
        Assertions.assertThat(searchWordResponseDto.get(1).getSearchWord()).isEqualTo("검색어3");
    }
}
