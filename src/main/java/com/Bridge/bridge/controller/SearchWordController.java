package com.Bridge.bridge.controller;

import com.Bridge.bridge.dto.response.SearchWordResponse;
import com.Bridge.bridge.service.SearchWordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchWordController {

    private final SearchWordService searchWordService;

    // 최근 검색어 불러오기 기능
    @GetMapping("/searchWords")
    @Operation(summary = "최근 검색어 조회 기능", description = "최근 검색어들을 조회할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "최근 검색어 조회 완료"),
            @ApiResponse(responseCode = "400", description = "최근 검색어 조회 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (Unauthorized)"),
            @ApiResponse(responseCode = "404", description = "유저 찾기 실패 OR 검색어가 존재하지 않는다.")
    })
    public List<SearchWordResponse> resentSearchWord(HttpServletRequest request){
        return searchWordService.resentSearchWord(request);
    }

    // 검색어 삭제 기능
    @DeleteMapping("/searchWord")
    @Operation(summary = "검색어 삭제 기능", description = "검색어를 삭제할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색어 삭제 완료"),
            @ApiResponse(responseCode = "400", description = "검색어 삭제 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (Unauthorized)"),
            @ApiResponse(responseCode = "404", description = "유저 찾기 실패 OR 검색어 찾기 실패")
    })
    public List<SearchWordResponse> deleteSearchWord(HttpServletRequest request, @RequestParam Long searchWordId){
        return searchWordService.deleteSearchWord(request, searchWordId);
    }

    // 검색어 삭제 기능
    @DeleteMapping("/searchWords")
    @Operation(summary = "검색어 전체 삭제 기능", description = "검색어를 모두 삭제할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색어 삭제 완료"),
            @ApiResponse(responseCode = "400", description = "검색어 삭제 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (Unauthorized)"),
            @ApiResponse(responseCode = "404", description = "유저 찾기 실패 OR 검색어 찾기 실패")
    })
    public ResponseEntity<?> deleteAllSearchWord(HttpServletRequest request){
        boolean result = searchWordService.deleteAllSearchWord(request);
        return ResponseEntity.ok(result);
    }
}
