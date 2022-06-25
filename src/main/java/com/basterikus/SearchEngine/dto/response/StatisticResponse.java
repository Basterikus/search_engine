package com.basterikus.SearchEngine.dto.response;

import com.basterikus.SearchEngine.dto.statistic.Statistics;
import lombok.Value;

@Value
public class StatisticResponse {
    boolean result;
    Statistics statistics;
}
