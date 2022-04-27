package com.basterikus.SearchEngine.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class PageDto {
    private String url;
    private CopyOnWriteArrayList<PageDto> subUrlList;
    private String htmlCode;
    private int statusCode;

    public void addSubUrl(PageDto page) {
        if (this.subUrlList == null) {
            this.subUrlList = new CopyOnWriteArrayList<>();
        }
        this.subUrlList.add(page);
    }

    public PageDto(String url) {
        this.url = url;
    }
}
