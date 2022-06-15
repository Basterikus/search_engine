package com.basterikus.SearchEngine.dto.statistic;

import lombok.Value;

@Value
public class TotalDto {
    Long sites;
    Long pages;
    Long lemmas;
    boolean isIndexing;
}
