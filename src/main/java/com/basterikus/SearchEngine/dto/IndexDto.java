package com.basterikus.SearchEngine.dto;


import lombok.Value;

@Value
public class IndexDto {
    Integer pageID;
    Integer lemmaID;
    Float rank;
}
