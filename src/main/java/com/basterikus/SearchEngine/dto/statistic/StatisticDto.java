package com.basterikus.SearchEngine.dto.statistic;

import lombok.Value;

import java.util.List;

@Value
public class StatisticDto {
    TotalDto totalDto;
    List<DetailedDto> detailedDto;
}
