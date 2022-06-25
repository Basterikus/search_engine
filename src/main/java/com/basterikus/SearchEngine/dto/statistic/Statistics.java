package com.basterikus.SearchEngine.dto.statistic;

import lombok.Value;

import java.util.List;

@Value
public class Statistics {
    Total total;
    List<Detailed> detailed;
}
