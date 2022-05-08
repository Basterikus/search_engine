package com.basterikus.SearchEngine.service.impl;

import com.basterikus.SearchEngine.dto.LemmaDto;
import com.basterikus.SearchEngine.dto.URLDto;
import com.basterikus.SearchEngine.model.Lemma;
import com.basterikus.SearchEngine.model.LemmaRepository;
import com.basterikus.SearchEngine.model.Page;
import com.basterikus.SearchEngine.model.PageRepository;
import com.basterikus.SearchEngine.parser.LinkPullParser;
import com.basterikus.SearchEngine.parser.Parser;
import com.basterikus.SearchEngine.parser.SiteMapper;
import com.basterikus.SearchEngine.service.PageURLService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Service
@RequiredArgsConstructor
public class PageService implements PageURLService {
    private static ForkJoinPool forkJoinPool;
    private static final int processorCoreCount = Runtime.getRuntime().availableProcessors();
    private final PageRepository pageRepository;
    private String urlFormat;
    private final Parser parser;
    private final LemmaRepository lemmaRepository;


    @Override
    public void getAllPagesFromUrl(String url) {
        urlFormat = "https://" + url + "/";
//        SiteMapBuilder siteMapBuilder = new SiteMapBuilder(urlFormat, false);
        SiteMapper siteMapper = new SiteMapper();
//        List<String> linkList = new ArrayList<>();
        List<URLDto> urlDtoList = new ArrayList<>();
        Thread workThread = new Thread(() -> {
            forkJoinPool = new ForkJoinPool(processorCoreCount);
            var link = forkJoinPool.invoke(new LinkPullParser(urlFormat, false));
            var siteMap = stringToList(link);
            for (String tempUrl : siteMap) {
                System.out.println("Get information from: " + tempUrl);
                var urlDto = siteMapper.getUrlDto(tempUrl);
                urlDtoList.add(urlDto);
            }
            System.out.println("Save to base");
            saveToBase(urlDtoList);
        });
        workThread.start();

    }

    @Override
    public void getLemma(String lemma) {
        String lem = lemma;
        System.out.println("Starting lemma");
        parser.parse();
        var lemmaList = parser.getLemmaDtoList();
//        var indexList = parser.getIndexDtoList();
        System.out.println("Starting save Lemma");
        for (LemmaDto lemmaDto : lemmaList) {
            lemmaRepository.save(new Lemma(lemmaDto.getLemma(), lemmaDto.getFrequency()));
        }
//        List<Lemma> lemmaBDList = (List<Lemma>) lemmaRepository.findAll();
    }

    private List<String> stringToList(String text) {
        return Arrays.stream(text.split("\n")).toList();
    }

//    private List<URLDto> getPageList(URLDto urlDto) {
//        List<URLDto> result = new ArrayList<>();
//        result.add(urlDto);
//        if (urlDto.getSubUrlList() != null) {
//            for (URLDto page : urlDto.getSubUrlList()) {
//                List<URLDto> tempPageList = getPageList(page);
//                result.addAll(tempPageList);
//            }
//        }
//        return result;
//    }

    private void saveToBase(List<URLDto> pages) {
        for (URLDto page : pages) {
            String pageFormat = page.getUrl().replace(urlFormat, "") + "/";
            pageRepository.save(new Page(pageFormat,
                    page.getStatusCode(),
                    page.getHtmlCode()));
        }
    }
}
