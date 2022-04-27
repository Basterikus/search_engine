package com.basterikus.SearchEngine.parser;

import com.basterikus.SearchEngine.dto.PageDto;
import com.basterikus.SearchEngine.utils.RandomUserAgent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class LinkPullParser extends RecursiveTask<PageDto> {
    private String parentUrl;
    private PageDto url;
    private volatile Set<String> usedUrl;
    private volatile Queue<PageDto> queue;

    public LinkPullParser(String parentUrl) {
        this.parentUrl = parentUrl;
        this.url = new PageDto(parentUrl);
        usedUrl = Collections.synchronizedSet(new HashSet<>());
        usedUrl.add(parentUrl);
        this.queue = new ConcurrentLinkedQueue<>();
    }

    public LinkPullParser(String parentUrl, Set<String> usedUrl, PageDto url, Queue<PageDto> queue) {
        this.parentUrl = parentUrl;
        this.usedUrl = usedUrl;
        this.url = url;
        this.queue = queue;
    }

    @Override
    protected PageDto compute() {
        List<LinkPullParser> taskList = new ArrayList<>();
        String currentUrl = url.getUrl();
        Document doc = getConnect(currentUrl);
        url.setHtmlCode(doc.outerHtml());
        var response = doc.connection().response();
        url.setStatusCode(response.statusCode());

        HashSet<String> links = getUrlList(doc);
        for (String link : links) {
            if (!usedUrl.contains(link)) {
                PageDto newPage = new PageDto(link);
                queue.add(newPage);
                usedUrl.add(link);
            }
        }

        while (queue.peek() != null) {
            PageDto tempPage = queue.poll();
            LinkPullParser task = new LinkPullParser(parentUrl, usedUrl, tempPage, queue);
            url.addSubUrl(tempPage);
            task.fork();
            taskList.add(task);
        }

        taskList.forEach(ForkJoinTask::join);

        return url;
    }

    public HashSet<String> getUrlList(Document doc) {
        HashSet<String> urlList = new HashSet<>();
        System.out.println("Parsing URL with address: " + parentUrl);
        Elements elements = doc.select("a");

        for (Element el : elements) {
            String attr = el.attr("abs:href");
            if (!attr.isEmpty() && !attr.contains("#") && !usedUrl.contains(attr) && attr.startsWith(parentUrl)) {
                LinkPullParser childUrl = new LinkPullParser(attr);
                childUrl.fork();
                urlList.add(attr);
            }
        }
        return urlList;
    }

    public Document getConnect(String url) {
        Document doc = null;
        try {
            Thread.sleep(150);
            doc = Jsoup.connect(parentUrl)
                    .userAgent(RandomUserAgent.getRandomUserAgent())
                    .referrer("http://www.google.com")
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

}
