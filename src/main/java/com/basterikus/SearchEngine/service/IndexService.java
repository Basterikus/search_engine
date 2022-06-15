package com.basterikus.SearchEngine.service;

public interface IndexService {
    void indexUrl(String url);
    void indexAll();
    void stopIndexing();
}
