package com.basterikus.SearchEngine.service.impl;

import com.basterikus.SearchEngine.dto.IndexDto;
import com.basterikus.SearchEngine.dto.LemmaDto;
import com.basterikus.SearchEngine.dto.PageDto;
import com.basterikus.SearchEngine.dto.SearchTextDto;
import com.basterikus.SearchEngine.model.*;
import com.basterikus.SearchEngine.parser.IndexParser;
import com.basterikus.SearchEngine.parser.PageUrlParser;
import com.basterikus.SearchEngine.parser.LemmaParser;
import com.basterikus.SearchEngine.repository.IndexRepository;
import com.basterikus.SearchEngine.repository.LemmaRepository;
import com.basterikus.SearchEngine.repository.PageRepository;
import com.basterikus.SearchEngine.search.SearchText;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ForkJoinPool;

@Service
@RequiredArgsConstructor
public class PageServiceImpl implements com.basterikus.SearchEngine.service.PageService {
    private static ForkJoinPool forkJoinPool;
    private static final int processorCoreCount = Runtime.getRuntime().availableProcessors();
    private final PageRepository pageRepository;
    private String url;
    private final LemmaParser lemmaParser;
    private final LemmaRepository lemmaRepository;
    private final IndexParser indexParser;
    private final IndexRepository indexRepository;
    private final SearchText searchTexting;


    @Override
    public void getAllPagesFromUrl(String url) {
        this.url = url;
        String urlFormat = "https://" + url + "/";
        List<PageDto> pageDtoList = new ArrayList<>();
        List<PageDto> pageDtos = new Vector<>();
        Thread workThread = new Thread(() -> {
            forkJoinPool = new ForkJoinPool(processorCoreCount);
            var pages = forkJoinPool.invoke(new PageUrlParser(urlFormat, pageDtos));
            pageDtoList.addAll(pages);
            System.out.println("Save to base");
            saveToBase(pageDtoList);
        });
        workThread.start();

    }

    @Override
    public void getLemmasFromPages() {
        System.out.println("Getting lemmas");
        lemmaParser.parse();
        var lemmaDtoList = lemmaParser.getLemmaDtoList();
        System.out.println("Starting save Lemma");
        List<Lemma> lemmaList = new ArrayList<>();
        for (LemmaDto lemmaDto : lemmaDtoList) {
            lemmaList.add(new Lemma(lemmaDto.getLemma(), lemmaDto.getFrequency()));
        }
        lemmaRepository.saveAll(lemmaList);
    }

    @Override
    public void indexingWords() {
        System.out.println("Starting indexing");
        indexParser.indexPage();
        var indexDtoList = indexParser.getIndexList();
        System.out.println("Starting new indexList");
        List<Index> indexList = new ArrayList<>();
        for (IndexDto indexDto : indexDtoList) {
            var page = pageRepository.getById(indexDto.getPageID());
            var lemma = lemmaRepository.getById(indexDto.getLemmaID());
            indexList.add(new Index(page, lemma, indexDto.getRank()));
        }
        System.out.println("Starting save to db");
        indexRepository.saveAll(indexList);
    }

    @Override
    public List<SearchTextDto> searchText(String text) {
        return searchTexting.search(text);
    }

    private void saveToBase(List<PageDto> pages) {
        List<Page> pageList = new ArrayList<>();
        for (PageDto page : pages) {
            int start = page.getUrl().indexOf(url) + url.length();
            String pageFormat = page.getUrl().substring(start);
            pageList.add(new Page(pageFormat,
                    page.getStatusCode(),
                    page.getHtmlCode()));
        }
        pageRepository.saveAll(pageList);
    }
}
