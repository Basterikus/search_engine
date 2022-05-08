package com.basterikus.SearchEngine.parser;

import com.basterikus.SearchEngine.dto.URLDto;
import com.basterikus.SearchEngine.utils.RandomUserAgent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

@NoArgsConstructor
public class SiteMapper {

    public URLDto getUrlDto(String currentUrl) {
        String htmlCode = "";
        int status = 500;
        try {
            var doc = getConnect(currentUrl);
            htmlCode = doc.outerHtml();
            var response = doc.connection().response();
            status = response.statusCode();
        } catch (Exception e) {
            System.out.println("Bad connect");
        }
        return new URLDto(currentUrl, htmlCode, status);
    }

    private Document getConnect(String url) {
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
