package com.basterikus.SearchEngine.parser;

import com.basterikus.SearchEngine.dto.IndexDto;
import com.basterikus.SearchEngine.dto.LemmaDto;

import java.util.List;

public interface Parser {
    void parse();
    List<LemmaDto> getLemmaDtoList();
    List<IndexDto> getIndexDtoList();
}
