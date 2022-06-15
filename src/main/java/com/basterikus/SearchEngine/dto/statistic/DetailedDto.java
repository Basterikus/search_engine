package com.basterikus.SearchEngine.dto.statistic;

import com.basterikus.SearchEngine.model.Status;
import lombok.Value;

import java.util.Date;

@Value
public class DetailedDto {
    String url;
    String name;
    Status status;
    Date statusTime;
    String error;
    Long pages;
    Long lemmas;
}
