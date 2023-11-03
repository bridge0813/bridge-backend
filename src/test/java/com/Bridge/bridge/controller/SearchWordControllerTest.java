package com.Bridge.bridge.controller;

import com.Bridge.bridge.domain.Platform;
import com.Bridge.bridge.domain.SearchWord;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.repository.SearchWordRepository;
import com.Bridge.bridge.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class SearchWordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SearchWordRepository searchWordRepository;

    @Test
    @DisplayName("최근 검색어 조회")
    @Transactional
    void resentSearch() throws Exception {
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
        userRepository.save(user);

        // when
        mockMvc.perform(get("/searchWords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(user.getId())))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].searchWord").value("검색어1"))
                .andDo(print());

    }

    @Test
    @DisplayName("최근 검색어 삭제")
    void deleteSearchWord() throws Exception {
        // given
        User user = new User("searchWord", "searchWord@gmail.com", Platform.APPLE, "searchWordTest");
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
        mockMvc.perform(delete("/searchWords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(user.getId()))
                        .content(objectMapper.writeValueAsString(newSearch1.getId())))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].searchWord").value("검색어2"))
                .andExpect(jsonPath("$[1].searchWord").value("검색어3"))
                .andDo(print());

    }
}
