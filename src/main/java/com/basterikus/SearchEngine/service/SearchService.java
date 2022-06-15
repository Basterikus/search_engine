package com.basterikus.SearchEngine.service;

import com.basterikus.SearchEngine.dto.SearchTextDto;

import java.util.List;


public interface SearchService {
    List<SearchTextDto> searchText(String text);

}
