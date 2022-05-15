package com.basterikus.SearchEngine.parser;

import com.basterikus.SearchEngine.dto.LemmaDto;

import java.util.List;

public interface LemmaParser {
    void parse();
    List<LemmaDto> getLemmaDtoList();
}
