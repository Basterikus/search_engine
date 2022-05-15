package com.basterikus.SearchEngine.dto;

import lombok.*;


@Value
public class PageDto {
    String url;
    String htmlCode;
    int statusCode;
}
