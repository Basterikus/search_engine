package com.basterikus.SearchEngine.morphology;

import java.util.HashMap;
import java.util.List;

public interface Morphology {
    HashMap<String, Integer> getLemmaList(String content);
    List<String> getLemma(String word);
    List<Integer> findLemmaIndex(String content, String lemma);
}
