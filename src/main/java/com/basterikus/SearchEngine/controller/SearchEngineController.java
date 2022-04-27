package com.basterikus.SearchEngine.controller;

import com.basterikus.SearchEngine.service.PageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchEngineController {

    private final PageService pageService;

    @GetMapping("/pages/{url}")
    public int getPages(@PathVariable String url) {
        String pageUrl = "https://www." + url + "/";
        pageService.getAllPages(pageUrl);
        return 1;
    }
}
