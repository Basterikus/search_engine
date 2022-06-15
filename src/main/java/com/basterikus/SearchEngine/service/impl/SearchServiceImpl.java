package com.basterikus.SearchEngine.service.impl;

import com.basterikus.SearchEngine.dto.SearchTextDto;
import com.basterikus.SearchEngine.search.SearchText;
import com.basterikus.SearchEngine.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final SearchText searchTexting;

    @Override
    public List<SearchTextDto> searchText(String text) {
        return searchTexting.search(text);
    }
}
