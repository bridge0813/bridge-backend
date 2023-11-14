package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.SearchWord;
import com.Bridge.bridge.domain.User;
import com.Bridge.bridge.dto.response.SearchWordResponseDto;
import com.Bridge.bridge.exception.notfound.NotFoundSearchWordException;
import com.Bridge.bridge.exception.notfound.NotFoundUserException;
import com.Bridge.bridge.repository.SearchWordRepository;
import com.Bridge.bridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchWordService {

    private final UserRepository userRepository;
    private final SearchWordRepository searchWordRepository;

    /*
        Func : 최근 검색어 조회 기능
        Parameter : userId
        Return : List<SearchWordResponseDto>
    */
    public List<SearchWordResponseDto> resentSearchWord(Long userId){
        // 해당 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        List<SearchWord> searchWords = user.getSearchWords();

        if(searchWords.isEmpty()){
            throw new NotFoundSearchWordException();
        }
        return searchWords.stream()
                .map((searchWord -> SearchWordResponseDto.builder()
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
    public List<SearchWordResponseDto> deleteSearchWord(Long userId, Long searchWordId){
        // 해당 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        // 해당 검색어 찾기
        SearchWord theSearchWord = searchWordRepository.findById(searchWordId)
                .orElseThrow(()-> new NotFoundSearchWordException());

        user.getSearchWords().remove(theSearchWord);
        searchWordRepository.delete(theSearchWord);

        return user.getSearchWords().stream()
                .map((searchWord -> SearchWordResponseDto.builder()
                        .searchWordId(searchWord.getId())
                        .searchWord(searchWord.getContent())
                        .build()))
                .collect(Collectors.toList());
    }
}
