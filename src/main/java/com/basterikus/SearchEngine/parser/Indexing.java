package com.basterikus.SearchEngine.parser;

import com.basterikus.SearchEngine.model.*;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Indexing {
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final FieldRepository fieldRepository;

    public void indexPage() {
        List<Page> pageList = (List<Page>) pageRepository.findAll();
        List<Lemma> lemmaList = (List<Lemma>) lemmaRepository.findAll();
        List<Field> fieldList = (List<Field>) fieldRepository.findAll();

        for (Page page : pageList) {
            var id = page.getId();
            var content = page.getContent();
            var title = clearElements(content, fieldList.get(0).getSelector());
            var body = clearElements(content, fieldList.get(1).getSelector());
            for (Lemma lemma : lemmaList) {

            }
        }

    }

    private String clearElements(String content, String selector) {
        StringBuilder html = new StringBuilder();
        var doc = Jsoup.parse(content);
        var elements = doc.select(selector);
        for (Element el : elements) {
            html.append(el.html());
        }
        return Jsoup.parse(html.toString()).text();
    }
}
