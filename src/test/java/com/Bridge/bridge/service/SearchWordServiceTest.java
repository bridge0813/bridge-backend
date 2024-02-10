package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.Platform;
import com.Bridge.bridge.domain.SearchWord;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.response.SearchWordResponse;
import com.Bridge.bridge.repository.SearchWordRepository;
import com.Bridge.bridge.repository.UserRepository;
import com.Bridge.bridge.security.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

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

    @Autowired
    private ProjectService projectService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("최근 검색어 조회")
    @Test
    void resentSearchWord() {
        // given
        User user = new User("user1", Platform.APPLE, "Test");
        user = userRepository.save(user);

        MockHttpServletRequest request = new MockHttpServletRequest();

        String token = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        request.addHeader("Authorization", "Bearer " + token);

        // when
        projectService.findByTitleAndContent(request, "검색어1");
        projectService.findByTitleAndContent(request, "검색어2");
        projectService.findByTitleAndContent(request, "검색어3");
        List<SearchWordResponse> searchWordResponse = searchWordService.resentSearchWord(request);

        // then
        Assertions.assertThat(searchWordResponse.get(0).getSearchWord()).isEqualTo("검색어3");
        Assertions.assertThat(searchWordResponse.get(1).getSearchWord()).isEqualTo("검색어2");
        Assertions.assertThat(searchWordResponse.get(2).getSearchWord()).isEqualTo("검색어1");
    }

    @DisplayName("최근 검색어 조회_최근 검색어가 없는 경우 빈 배열 반환")
    @Test
    void resentSearchWord_nothing() {
        // given
        User user = new User("user1", Platform.APPLE, "Test");
        user = userRepository.save(user);

        MockHttpServletRequest request = new MockHttpServletRequest();

        String token = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        request.addHeader("Authorization", "Bearer " + token);

        // when
        List<SearchWordResponse> searchWordResponse = searchWordService.resentSearchWord(request);
        Assertions.assertThat(searchWordResponse.size()).isEqualTo(0);
    }

    @DisplayName("최근 검색어 삭제")
    @Test
    void deleteSearchWord() {
        // given
        User user = new User("user1", Platform.APPLE, "Test");
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

        MockHttpServletRequest request = new MockHttpServletRequest();

        String token = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        request.addHeader("Authorization", "Bearer " + token);

        // when
        List<SearchWordResponse> searchWordResponse = searchWordService.deleteSearchWord(request, newSearch1.getId());
        Assertions.assertThat(searchWordResponse.get(0).getSearchWord()).isEqualTo("검색어2");
        Assertions.assertThat(searchWordResponse.get(1).getSearchWord()).isEqualTo("검색어3");
    }

    @DisplayName("최근 검색어 전체 삭제")
    @Test
    void deleteAllSearchWord() {
        // given
        User user = new User("user1", Platform.APPLE, "Test");
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

        MockHttpServletRequest request = new MockHttpServletRequest();

        String token = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        request.addHeader("Authorization", "Bearer " + token);

        // when
        boolean result = searchWordService.deleteAllSearchWord(request);

        //then
        Assertions.assertThat(result).isEqualTo(true);

        List<SearchWord> searchWords = searchWordRepository.findAllByUserOrderByHistoryDesc(user);
        Assertions.assertThat(searchWords.size()).isEqualTo(0);
    }

    @DisplayName("최근 검색어 전체 삭제 - 저장된 검색어가 없을 때")
    @Test
    void deleteAllSearchWord_0_searchWord() {
        // given
        User user = new User("user1", Platform.APPLE, "Test");
        user = userRepository.save(user);

        MockHttpServletRequest request = new MockHttpServletRequest();

        String token = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .signWith(SignatureAlgorithm.HS256, jwtTokenProvider.getKey())
                .compact();

        request.addHeader("Authorization", "Bearer " + token);

        // when
        boolean result = searchWordService.deleteAllSearchWord(request);

        //then
        Assertions.assertThat(result).isEqualTo(true);

        List<SearchWord> searchWords = searchWordRepository.findAllByUserOrderByHistoryDesc(user);
        Assertions.assertThat(searchWords.size()).isEqualTo(0);
    }
}
