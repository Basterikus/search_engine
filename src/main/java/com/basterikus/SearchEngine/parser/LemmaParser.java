package com.basterikus.SearchEngine.parser;

import com.basterikus.SearchEngine.dto.LemmaDto;
import com.basterikus.SearchEngine.model.Site;

import java.util.List;

public interface LemmaParser {
    void run(Site site);
    List<LemmaDto> getLemmaDtoList();
}
