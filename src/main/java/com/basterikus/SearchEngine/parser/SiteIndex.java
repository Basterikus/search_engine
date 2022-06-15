package com.basterikus.SearchEngine.parser;

import com.basterikus.SearchEngine.config.IndexConfig;
import com.basterikus.SearchEngine.dto.IndexDto;
import com.basterikus.SearchEngine.dto.LemmaDto;
import com.basterikus.SearchEngine.dto.PageDto;
import com.basterikus.SearchEngine.model.*;
import com.basterikus.SearchEngine.repository.IndexRepository;
import com.basterikus.SearchEngine.repository.LemmaRepository;
import com.basterikus.SearchEngine.repository.PageRepository;
import com.basterikus.SearchEngine.repository.SiteRepository;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.concurrent.ForkJoinPool;

@RequiredArgsConstructor
public class SiteIndex implements Runnable {

    private static ForkJoinPool forkJoinPool;
    private static final int processorCoreCount = Runtime.getRuntime().availableProcessors();
    private final PageRepository pageRepository;
    private final LemmaParser lemmaParser;
    private final LemmaRepository lemmaRepository;
    private final IndexParser indexParser;
    private final IndexRepository indexRepository;
    private final SiteRepository siteRepository;
    private final String url;
    private final IndexConfig indexConfig;

    @Override
    public void run() {
        if (siteRepository.findByUrl(url) != null) {
            var site = siteRepository.findByUrl(url);
            site.setStatus(Status.INDEXING);
            site.setStatusTime(new Date());
            siteRepository.save(site);
            siteRepository.delete(site);
        }
        Site site = new Site();
        site.setUrl(url);
        site.setName(getName(url));
        site.setStatus(Status.INDEXING);
        site.setStatusTime(new Date());
        siteRepository.save(site);
        String urlFormat = url + "/";
        List<PageDto> pageDtoList = new ArrayList<>();
        List<PageDto> pageDtos = new Vector<>();
        Thread workThread = new Thread(() -> {
            forkJoinPool = new ForkJoinPool(processorCoreCount);
            var pages = forkJoinPool.invoke(new PageUrlParser(urlFormat, pageDtos));
            pageDtoList.addAll(pages);
            System.out.println("Save to base");
            saveToBase(pageDtoList, url);
            getLemmasFromPages(url);
            indexingWords(url);
        });
        workThread.start();
    }

    private void getLemmasFromPages(String url) {
        System.out.println("Getting lemmas");
        var site = siteRepository.findByUrl(url);
        site.setStatusTime(new Date());
        lemmaParser.parse(site);
        var lemmaDtoList = lemmaParser.getLemmaDtoList();
        System.out.println("Starting save Lemma");
        List<Lemma> lemmaList = new ArrayList<>();
        for (LemmaDto lemmaDto : lemmaDtoList) {
            lemmaList.add(new Lemma(lemmaDto.getLemma(), lemmaDto.getFrequency(), site));
        }
        lemmaRepository.saveAll(lemmaList);
    }

    private void indexingWords(String url) {
        System.out.println("Starting indexing");
        var site = siteRepository.findByUrl(url);
        indexParser.indexPage(site);
        var indexDtoList = indexParser.getIndexList();
        System.out.println("Starting new indexList");
        List<Index> indexList = new ArrayList<>();
        for (IndexDto indexDto : indexDtoList) {
            var page = pageRepository.getById(indexDto.getPageID());
            var lemma = lemmaRepository.getById(indexDto.getLemmaID());
            site.setStatusTime(new Date());
            indexList.add(new Index(page, lemma, indexDto.getRank()));
        }
        System.out.println("Starting save to db");
        indexRepository.saveAll(indexList);
        System.out.println("Indexing complete");
        site.setStatusTime(new Date());
        site.setStatus(Status.INDEXED);
        siteRepository.save(site);
    }

    private void saveToBase(List<PageDto> pages, String url) {
        List<Page> pageList = new ArrayList<>();
        var site = siteRepository.findByUrl(url);
        for (PageDto page : pages) {
            int start = page.getUrl().indexOf(url) + url.length();
            String pageFormat = page.getUrl().substring(start);
            pageList.add(new Page(pageFormat,
                    page.getStatusCode(),
                    page.getHtmlCode(),
                    site));
        }
        pageRepository.saveAll(pageList);
    }

    private String getName(String url) {
        var urlList = indexConfig.getSite();
        for (Map<String, String> map : urlList) {
            if (map.get("url").equals(url)) {
                return map.get("name");
            }
        }
        return "";
    }
}
