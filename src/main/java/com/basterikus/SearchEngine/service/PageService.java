package com.basterikus.SearchEngine.service;

import com.basterikus.SearchEngine.dto.SearchTextDto;

import java.util.List;

public interface PageService {
    void getAllPagesFromUrl(String url);
    void getLemmasFromPages();
    void indexingWords();
    List<SearchTextDto> searchText(String text);
}
