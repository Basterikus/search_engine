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
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;

@RequiredArgsConstructor
@Slf4j
public class SiteIndex implements Runnable {

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
            log.info("Начато удаление данных - " + url);
            var site = siteRepository.findByUrl(url);
            site.setStatus(Status.INDEXING);
            site.setStatusTime(new Date());
            siteRepository.save(site);
            siteRepository.delete(site);
        }
        log.info("Начата индексация - " + url);
        Site site = new Site();
        site.setUrl(url);
        site.setName(getName());
        site.setStatus(Status.INDEXING);
        site.setStatusTime(new Date());
        siteRepository.save(site);
        try {
            var pageDtoList = getPageDtoList();
            saveToBase(pageDtoList);
            getLemmasFromPages();
            indexingWords();
        } catch (Exception e) {
            log.error("Индексация остановлена - " + url);
            site.setLastError("Индексация остановлена");
            site.setStatus(Status.FAILED);
            site.setStatusTime(new Date());
            siteRepository.save(site);
        }
    }

    private List<PageDto> getPageDtoList() throws InterruptedException {
        if (!Thread.interrupted()) {
            String urlFormat = url + "/";
            List<PageDto> pageDtoVector = new Vector<>();
            List<String> urlList = new Vector<>();
            ForkJoinPool forkJoinPool = new ForkJoinPool(processorCoreCount);
            var pages = forkJoinPool.invoke(new PageUrlParser(urlFormat, pageDtoVector, urlList));
            return new CopyOnWriteArrayList<>(pages);
        } else throw new InterruptedException();
    }

    private void getLemmasFromPages() throws InterruptedException {
        if (!Thread.interrupted()) {
            var site = siteRepository.findByUrl(url);
            site.setStatusTime(new Date());
            lemmaParser.run(site);
            List<LemmaDto> lemmaDtoList = new CopyOnWriteArrayList<>(lemmaParser.getLemmaDtoList());
            List<Lemma> lemmaList = new CopyOnWriteArrayList<>();
            for (LemmaDto lemmaDto : lemmaDtoList) {
                lemmaList.add(new Lemma(lemmaDto.getLemma(), lemmaDto.getFrequency(), site));
            }
            lemmaRepository.saveAll(lemmaList);
        } else {
            throw new InterruptedException();
        }
    }

    private void indexingWords() throws InterruptedException {
        if (!Thread.interrupted()) {
            var site = siteRepository.findByUrl(url);
            indexParser.run(site);
            List<IndexDto> indexDtoList = new CopyOnWriteArrayList<>(indexParser.getIndexList());
            List<Index> indexList = new CopyOnWriteArrayList<>();
            for (IndexDto indexDto : indexDtoList) {
                if (!Thread.interrupted()) {
                    var page = pageRepository.getById(indexDto.getPageID());
                    var lemma = lemmaRepository.getById(indexDto.getLemmaID());
                    site.setStatusTime(new Date());
                    indexList.add(new Index(page, lemma, indexDto.getRank()));
                } else throw new InterruptedException();
            }
            indexRepository.saveAll(indexList);
            log.info("Индексация завершена - " + url);
            site.setStatusTime(new Date());
            site.setStatus(Status.INDEXED);
            siteRepository.save(site);
        } else {
            throw new InterruptedException();
        }
    }

    private void saveToBase(List<PageDto> pages) throws InterruptedException {
        if (!Thread.interrupted()) {
            List<Page> pageList = new CopyOnWriteArrayList<>();
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
        } else {
            throw new InterruptedException();
        }
    }

    private String getName() {
        var urlList = indexConfig.getSite();
        for (Map<String, String> map : urlList) {
            if (map.get("url").equals(url)) {
                return map.get("name");
            }
        }
        return "";
    }
}
