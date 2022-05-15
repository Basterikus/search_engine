package com.basterikus.SearchEngine.search;

import com.basterikus.SearchEngine.dto.SearchTextDto;

import java.util.List;

public interface SearchText {
    List<SearchTextDto> search(String text);
}
