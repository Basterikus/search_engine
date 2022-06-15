package com.basterikus.SearchEngine.controller;

import com.basterikus.SearchEngine.dto.SearchTextDto;
import com.basterikus.SearchEngine.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/api/search")
    public List<SearchTextDto> searchWords(
            @PathVariable String query,
            @PathVariable String site,
            @PathVariable int offset,
            @PathVariable int limit) {
        return searchService.searchText(query);
    }
}
