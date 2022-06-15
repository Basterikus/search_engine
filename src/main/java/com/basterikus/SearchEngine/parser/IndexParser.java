package com.basterikus.SearchEngine.parser;

import com.basterikus.SearchEngine.dto.IndexDto;
import com.basterikus.SearchEngine.model.Site;

import java.util.List;

public interface IndexParser {
    void indexPage(Site site);
    List<IndexDto> getIndexList();
}
