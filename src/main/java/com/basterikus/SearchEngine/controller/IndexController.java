package com.basterikus.SearchEngine.controller;

import com.basterikus.SearchEngine.service.IndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class IndexController {

    private final IndexService indexService;


    @GetMapping("/api/startIndexing")
    public int startIndexingAll() {
        indexService.indexAll();
        return 1;
    }

    @GetMapping("/api/stopIndexing")
    public int stopIndexing() {
        indexService.stopIndexing();
        return 1;
    }

    @PostMapping("/api/indexPage")
    public int startIndexingOne(String url) {
        indexService.indexUrl(url);
        return 1;
    }
}
