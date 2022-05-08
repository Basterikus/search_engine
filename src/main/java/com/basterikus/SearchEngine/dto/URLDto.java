package com.basterikus.SearchEngine.dto;

import lombok.*;


@Value
public class URLDto {
    String url;
    String htmlCode;
    int statusCode;
}
