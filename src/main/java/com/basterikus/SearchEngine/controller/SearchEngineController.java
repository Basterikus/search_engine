package com.basterikus.SearchEngine.controller;

import com.basterikus.SearchEngine.service.impl.PageService;
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
        pageService.getAllPagesFromUrl(url);
        return 1;
    }

    @GetMapping("/lemma/{lemma}")
    public int getLemma(@PathVariable String lemma) {
        pageService.getLemma(lemma);
        return 1;
    }
}
