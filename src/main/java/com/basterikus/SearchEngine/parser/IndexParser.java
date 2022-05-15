package com.basterikus.SearchEngine.parser;

import com.basterikus.SearchEngine.dto.IndexDto;

import java.util.List;

public interface IndexParser {
    void indexPage();
    List<IndexDto> getIndexList();
}
