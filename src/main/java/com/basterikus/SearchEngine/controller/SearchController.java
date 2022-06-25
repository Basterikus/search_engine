package com.basterikus.SearchEngine.controller;

import com.basterikus.SearchEngine.dto.SearchDto;
import com.basterikus.SearchEngine.dto.response.FalseResponse;
import com.basterikus.SearchEngine.dto.response.SearchResponse;
import com.basterikus.SearchEngine.repository.SiteRepository;
import com.basterikus.SearchEngine.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final SiteRepository siteRepository;

    @GetMapping("/search")
    public ResponseEntity<Object> searchWords(
            @RequestParam(name = "query", required = false, defaultValue = "") String query,
            @RequestParam(name = "site", required = false, defaultValue = "") String site,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "limit", required = false, defaultValue = "20") int limit) {
        if (query.isEmpty()) {
            return new ResponseEntity<>(new FalseResponse(false, "Задан пустой поисковый запрос"),
                    HttpStatus.BAD_REQUEST);
        } else {
            List<SearchDto> searchData;
            if (!site.isEmpty()) {
                if (siteRepository.findByUrl(site) == null) {
                    return new ResponseEntity<>(new FalseResponse(false, "Указанная страница не найдена"),
                            HttpStatus.BAD_REQUEST);
                } else {
                    searchData = searchService.siteSearch(query, site, offset, limit);
                }
            } else {
                searchData = searchService.allSiteSearch(query, offset, limit);
            }
            return new ResponseEntity<>(new SearchResponse(true, searchData.size(), searchData),
                    HttpStatus.OK);
        }
    }
}
