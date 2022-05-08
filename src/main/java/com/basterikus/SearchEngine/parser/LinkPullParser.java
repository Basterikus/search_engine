package com.basterikus.SearchEngine.parser;

import com.basterikus.SearchEngine.utils.RandomUserAgent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.concurrent.RecursiveTask;

public class LinkPullParser extends RecursiveTask<String> {
    private final String url;
    private final static List<String> urlList = new Vector<>();
//    private URLDto url;
//    private volatile Set<String> usedUrl;
//    private volatile Queue<URLDto> queue;
    private final boolean isInterrupted;

//    public LinkPullParser(String parentUrl) {
//        this.parentUrl = parentUrl;
//        this.url = new URLDto(parentUrl);
//        usedUrl = Collections.synchronizedSet(new HashSet<>());
//        usedUrl.add(parentUrl);
//        this.queue = new ConcurrentLinkedQueue<>();
//    }
//
//    public LinkPullParser(String parentUrl, Set<String> usedUrl, URLDto url, Queue<URLDto> queue) {
//        this.parentUrl = parentUrl;
//        this.usedUrl = usedUrl;
//        this.url = url;
//        this.queue = queue;
//    }
    public LinkPullParser(String url, boolean isInterrupted) {
        this.url = url;
        this.isInterrupted = isInterrupted;
    }

    @Override
    protected String compute() {
        if(isInterrupted) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        result.append(url);
        try {
            Thread.sleep(150);
            var doc = getConnect(url);
            String html = doc.outerHtml();
            var response = doc.connection().response();
            int status = response.statusCode();
            System.out.println("Parsing URL with address: " + url);
            var elements = doc.select("a");
            List<LinkPullParser> taskList = new ArrayList<>();
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
                    LinkPullParser task = new LinkPullParser(link, false);
                    task.fork();
                    taskList.add(task);
                }
            }

            for (LinkPullParser lpl : taskList) {
                var text = lpl.join();
                if (!text.equals("")) {
                    result.append("\n").append(text);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();


//        List<LinkPullParser> taskList = new ArrayList<>();
//        String currentUrl = url.getUrl();
//        Document doc = getConnect(currentUrl);
//        url.setHtmlCode(doc.outerHtml());
//        var response = doc.connection().response();
//        url.setStatusCode(response.statusCode());
//
//        HashSet<String> links = getUrlList(doc);
//        for (String link : links) {
//            if (!usedUrl.contains(link)) {
//                URLDto newPage = new URLDto(link);
//                queue.add(newPage);
//                usedUrl.add(link);
//            }
//        }
//
//        while (queue.peek() != null) {
//            URLDto tempPage = queue.poll();
//            LinkPullParser task = new LinkPullParser(parentUrl, usedUrl, tempPage, queue);
//            url.addSubUrl(tempPage);
//            task.fork();
//            taskList.add(task);
//        }
//
//        taskList.forEach(ForkJoinTask::join);

//        return url;
    }

//    public HashSet<String> getUrlList(Document doc) {
//        HashSet<String> urlList = new HashSet<>();
//        System.out.println("Parsing URL with address: " + parentUrl);
//        Elements elements = doc.select("a");
//
//        for (Element el : elements) {
//            String attr = el.attr("abs:href");
//            if (!attr.isEmpty() && !attr.contains("#") && !usedUrl.contains(attr) && attr.startsWith(parentUrl)) {
//                LinkPullParser childUrl = new LinkPullParser(attr);
//                childUrl.fork();
//                urlList.add(attr);
//            }
//        }
//        return urlList;
//    }

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
