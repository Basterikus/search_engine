package com.basterikus.SearchEngine.dto.response;

import com.basterikus.SearchEngine.dto.SearchDto;
import lombok.Value;

import java.util.List;

@Value
public class SearchResponse {
    boolean result;
    int count;
    List<SearchDto> data;
}
