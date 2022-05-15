package com.basterikus.SearchEngine.dto;

import lombok.Value;

@Value
public class SearchTextDto {
    String uri;
    String title;
    String snippet;
    Float relevance;
}
