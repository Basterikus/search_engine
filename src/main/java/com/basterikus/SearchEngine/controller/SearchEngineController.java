package com.basterikus.SearchEngine.controller;

import com.basterikus.SearchEngine.dto.SearchTextDto;
import com.basterikus.SearchEngine.service.PageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public int getLemma() {
        pageService.getLemmasFromPages();
        return 1;
    }

    @GetMapping("/index/{index}")
    public int getIndex() {
        pageService.indexingWords();
        return 1;
    }

    @GetMapping("search/{text}")
    public List<SearchTextDto> searchWords(@PathVariable String text) {
        return pageService.searchText(text);
    }
}
