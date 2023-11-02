package com.Bridge.bridge.controller;

import com.Bridge.bridge.dto.response.SearchWordResponseDto;
import com.Bridge.bridge.service.ProjectService;
import com.Bridge.bridge.service.SearchWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchWordController {

    private SearchWordService searchWordService;

    // 최근 검색어 불러오기 기능
    @GetMapping("/searchWords")
    public List<SearchWordResponseDto> resentSearchWord(@RequestParam("userId") Long userId){
        return searchWordService.resentSearchWord(userId);
    }

    // 검색어 삭제 기능
    @DeleteMapping("/searchWords")
    public List<SearchWordResponseDto> deleteSearchWord(@RequestParam("userId") Long userId, @RequestBody Long searchWordId){
        return searchWordService.deleteSearchWord(userId, searchWordId);
    }
}
