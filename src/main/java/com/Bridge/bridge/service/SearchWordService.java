package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.SearchWord;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.response.SearchWordResponse;
import com.Bridge.bridge.exception.notfound.NotFoundSearchWordException;
import com.Bridge.bridge.exception.notfound.NotFoundUserException;
import com.Bridge.bridge.repository.SearchWordRepository;
import com.Bridge.bridge.repository.UserRepository;
import com.Bridge.bridge.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchWordService {

    private final UserRepository userRepository;
    private final SearchWordRepository searchWordRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /*
        Func : 최근 검색어 조회 기능
        Parameter : userId
        Return : List<SearchWordResponseDto>
    */
    public List<SearchWordResponse> resentSearchWord(HttpServletRequest request){

        Long userId = jwtTokenProvider.getUserIdFromRequest(request);

        // 해당 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        List<SearchWord> searchWords = searchWordRepository.findAllByUserOrderByHistoryDesc(user);

        if(searchWords.isEmpty()){
            return new ArrayList<>();
        }
        return searchWords.stream()
                .map((searchWord -> SearchWordResponse.builder()
                        .searchWordId(searchWord.getId())
                        .searchWord(searchWord.getContent())
                        .build()))
                .collect(Collectors.toList());
    }

    /*
        Func : 최근 검색어 삭제 기능
        Parameter : userId, searchWordId
        Return : List<SearchWordResponseDto>
    */
    @Transactional
    public List<SearchWordResponse> deleteSearchWord(HttpServletRequest request, Long searchWordId){

        Long userId = jwtTokenProvider.getUserIdFromRequest(request);

        // 해당 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        // 해당 검색어 찾기
        SearchWord theSearchWord = searchWordRepository.findById(searchWordId)
                .orElseThrow(()-> new NotFoundSearchWordException());

        user.getSearchWords().remove(theSearchWord);
        searchWordRepository.delete(theSearchWord);

        return user.getSearchWords().stream()
                .map((searchWord -> SearchWordResponse.builder()
                        .searchWordId(searchWord.getId())
                        .searchWord(searchWord.getContent())
                        .build()))
                .collect(Collectors.toList());
    }

    /*
        Func : 검색어 전체 삭제
        Parameter : userId
        Return : List<SearchWordResponseDto>
    */
    @Transactional
    public boolean deleteAllSearchWord(HttpServletRequest request){
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);

        // 해당 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        try {
            searchWordRepository.deleteAllByUser(user);
        }
        catch (Exception e){
            System.out.println(e);
        }

        return true;
    }
}
