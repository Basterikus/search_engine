package com.basterikus.SearchEngine.dto;

import lombok.Value;

@Value
public class IndexDto {
    int pageId;
    String lemma;
    float rank;
}
