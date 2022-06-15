package com.basterikus.SearchEngine.parser;

import com.basterikus.SearchEngine.dto.IndexDto;
import com.basterikus.SearchEngine.model.*;
import com.basterikus.SearchEngine.morphology.Morphology;
import com.basterikus.SearchEngine.repository.FieldRepository;
import com.basterikus.SearchEngine.repository.LemmaRepository;
import com.basterikus.SearchEngine.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Indexing implements IndexParser {
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final FieldRepository fieldRepository;
    private final Morphology morphology;
    private List<IndexDto> indexDtoList;

    @Override
    public void indexPage(Site site) {
        List<Page> pageList = pageRepository.findBySite(site);
        List<Lemma> lemmaList = lemmaRepository.findBySite(site);
        List<Field> fieldList = fieldRepository.findAll();
        indexDtoList = new ArrayList<>();

        for (Page page : pageList) {
            if (page.getStatusCode() == 200) {
                System.out.println("Getting word from page" + page.getPath());
                Integer pageId = page.getId();
                var content = page.getContent();
                var title = clearElements(content, fieldList.get(0).getSelector());
                var body = clearElements(content, fieldList.get(1).getSelector());
                var titleList = morphology.getLemmaList(title);
                var bodyList = morphology.getLemmaList(body);

                for (Lemma lemma : lemmaList) {
                    Integer lemmaId = lemma.getId();
                    String keyWord = lemma.getLemma();
                    if (titleList.containsKey(keyWord) || bodyList.containsKey(keyWord)) {
                        float totalRank = 0.0F;
                        if (titleList.get(keyWord) != null) {
                            Float titleRank = Float.valueOf(titleList.get(keyWord));
                            totalRank += titleRank;
                        }
                        if (bodyList.get(keyWord) != null) {
                            float bodyRank = (float) (bodyList.get(keyWord) * 0.8);
                            totalRank += bodyRank;
                        }
                        indexDtoList.add(new IndexDto(pageId, lemmaId, totalRank));
                    } else {
                        System.out.println("Lemma not found");
                    }
                }
            } else {
                System.out.println("Bad status code");
            }
        }
    }

    @Override
    public List<IndexDto> getIndexList() {
        return indexDtoList;
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
