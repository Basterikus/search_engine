package com.basterikus.SearchEngine.service;

import com.basterikus.SearchEngine.dto.PageDto;
import com.basterikus.SearchEngine.model.Page;
import com.basterikus.SearchEngine.model.PageRepository;
import com.basterikus.SearchEngine.parser.LinkPullParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Service
@RequiredArgsConstructor
public class PageService implements PageURLService{
    private static ForkJoinPool forkJoinPool;
    private static final int processorCoreCount = Runtime.getRuntime().availableProcessors();
    private final PageRepository pageRepository;
    @Override
    public void getAllPages(String url) {
        Thread workThread = new Thread(() -> {

            forkJoinPool = new ForkJoinPool(processorCoreCount);
            PageDto pageDto = forkJoinPool.invoke(new LinkPullParser(url));

            saveToBase(getPages(pageDto));
        });

        workThread.start();

    }

    public List<PageDto> getPages(PageDto pageDto) {
        List<PageDto> result = new ArrayList<>();

        result.add(pageDto);
        if (pageDto.getSubUrlList() != null) {
            for (PageDto page : pageDto.getSubUrlList()) {
                List<PageDto> tempPageList = getPages(page);
                result.addAll(tempPageList);
            }
        }
        return result;
    }

    public void saveToBase(List<PageDto> pages) {
//        pageRepository.saveAll(pages);
        for (PageDto page : pages) {
            pageRepository.save(new Page(page.getUrl(), page.getStatusCode(), page.getHtmlCode()));
        }
    }
}
