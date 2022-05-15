package com.basterikus.SearchEngine.parser;

import com.basterikus.SearchEngine.dto.PageDto;
import com.basterikus.SearchEngine.utils.RandomUserAgent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class PageUrlParser extends RecursiveTask<List<PageDto>> {
    private final String url;
    private final static List<String> urlList = new Vector<>();
    private final List<PageDto> pageDtoList;

    public PageUrlParser(String url, List<PageDto> pageDtoList) {
        this.url = url;
        this.pageDtoList = pageDtoList;
    }

    @Override
    protected List<PageDto> compute() {
        try {
            Thread.sleep(150);
            var doc = getConnect(url);
            String html = doc.outerHtml();
            var response = doc.connection().response();
            int status = response.statusCode();
            PageDto pageDto = new PageDto(url, html, status);
            pageDtoList.add(pageDto);
            System.out.println("Parsing URL with address: " + url);
            var elements = doc.select("a");
            List<PageUrlParser> taskList = new ArrayList<>();
            for (Element el : elements) {
                var link = el.attr("abs:href");
                if (link.startsWith(el.baseUri()) &&
                        !link.equals(el.baseUri()) &&
                        !link.contains("#") &&
                        !link.contains(".pdf") &&
                        !link.contains(".jpg") &&
                        !link.contains(".JPG") &&
                        !link.contains(".png") &&
                        !urlList.contains(link)) {
                    urlList.add(link);
                    PageUrlParser task = new PageUrlParser(link, pageDtoList);
                    task.fork();
                    taskList.add(task);
                }
            }

            taskList.forEach(ForkJoinTask::join);
        } catch (Exception e) {
            e.printStackTrace();
            PageDto pageDto = new PageDto(url, "", 500);
            pageDtoList.add(pageDto);
        }
        return pageDtoList;
    }

    public Document getConnect(String url) {
        Document doc = null;
        try {
            Thread.sleep(150);
            doc = Jsoup.connect(url)
                    .userAgent(RandomUserAgent.getRandomUserAgent())
                    .referrer("http://www.google.com")
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

}
