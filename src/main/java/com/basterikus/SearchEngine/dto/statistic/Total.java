package com.basterikus.SearchEngine.dto.statistic;

import lombok.Value;

@Value
public class Total {
    Long sites;
    Long pages;
    Long lemmas;
    boolean isIndexing;
}
