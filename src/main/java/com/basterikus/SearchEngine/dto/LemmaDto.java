package com.basterikus.SearchEngine.dto;

import lombok.Value;

@Value
public class LemmaDto {
    String lemma;
    int frequency;
}
