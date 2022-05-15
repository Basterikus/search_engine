package com.basterikus.SearchEngine.parser;

import com.basterikus.SearchEngine.dto.LemmaDto;
import com.basterikus.SearchEngine.model.Field;
import com.basterikus.SearchEngine.repository.FieldRepository;
import com.basterikus.SearchEngine.model.Page;
import com.basterikus.SearchEngine.repository.PageRepository;
import com.basterikus.SearchEngine.morphology.Morphology;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class LemmaConversion implements LemmaParser {
    private final PageRepository pageRepository;
    private final FieldRepository fieldRepository;
    private final Morphology morphology;
    private List<LemmaDto> lemmaDtoList;

    @Override
    public List<LemmaDto> getLemmaDtoList() {
        return lemmaDtoList;
    }

    @Override
    public void parse() {
        lemmaDtoList = new ArrayList<>();
        List<Page> pageList = (List<Page>) pageRepository.findAll();
        List<Field> fieldList = (List<Field>) fieldRepository.findAll();
        HashMap<String, Integer> lemmaList = new HashMap<>();
        for (Page page : pageList) {
            var content = page.getContent();
            var title = clearElements(content, fieldList.get(0).getSelector());
            var body = clearElements(content, fieldList.get(1).getSelector());
            var titleList = morphology.getLemmaList(title);
            var bodyList = morphology.getLemmaList(body);

            Set<String> allWords = new HashSet<>();
            allWords.addAll(titleList.keySet());
            allWords.addAll(bodyList.keySet());
            for (String word : allWords) {
                int frequency = lemmaList.getOrDefault(word, 0);
                lemmaList.put(word, frequency + 1);
            }
        }

        for (String lemma : lemmaList.keySet()) {
            var frequency = lemmaList.get(lemma);
            lemmaDtoList.add(new LemmaDto(lemma, frequency));
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
