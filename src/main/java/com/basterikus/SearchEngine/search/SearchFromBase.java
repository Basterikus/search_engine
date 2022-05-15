package com.basterikus.SearchEngine.search;

import com.basterikus.SearchEngine.dto.SearchTextDto;
import com.basterikus.SearchEngine.model.*;
import com.basterikus.SearchEngine.morphology.Morphology;
import com.basterikus.SearchEngine.repository.FieldRepository;
import com.basterikus.SearchEngine.repository.LemmaRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SearchFromBase implements SearchText {
    private final Morphology morphology;
    private final LemmaRepository lemmaRepository;
    private final FieldRepository fieldRepository;

    @Override
    public List<SearchTextDto> search(String searchText) {
        var elements = searchText.toLowerCase(Locale.ROOT).split("\\s+");
        var fieldList = fieldRepository.findAll();
        List<String> searchWordsLemmaList = new ArrayList<>();
        List<Lemma> findingLemmaList = new ArrayList<>();
        HashMap<Page, Integer> pageFrequencyList = new HashMap<>();
        List<Page> findingPageList = new ArrayList<>();
        List<Index> findingIndexList = new ArrayList<>();
        List<SearchTextDto> searchTextDtoList = new ArrayList<>();
        for (String el : elements) {
            var lemmaWord = morphology.getLemma(el);
            searchWordsLemmaList.addAll(lemmaWord);
        }
        for (String searchLemma : searchWordsLemmaList) {
            var lemma = lemmaRepository.findByLemma(searchLemma);
            findingLemmaList.add(lemma);
        }
        findingLemmaList.sort(Comparator.comparingInt(Lemma::getFrequency));

        for (Lemma lemma : findingLemmaList) {
            var indexList = lemma.getIndex();
            for (Index index : indexList) {
                var page = index.getPage();
                var count = pageFrequencyList.getOrDefault(page, 0);
                pageFrequencyList.put(page, count + 1);
            }
        }
        for (Page page : pageFrequencyList.keySet()) {
            var count = pageFrequencyList.get(page);
            if (count >= findingLemmaList.size()) {
                findingPageList.add(page);
            }
        }

        for (Lemma lemma : findingLemmaList) {
            var indexList = lemma.getIndex();
            for (Index index : indexList) {
                var page = index.getPage();
                if (findingPageList.contains(page)) {
                    findingIndexList.add(index);
                }
            }
        }

        HashMap<Page, Float> pageWithRelevance = new HashMap<>();
        for (Page page : findingPageList) {
            float relevant = 0;
            for (Index index : findingIndexList) {
                if (index.getPage() == page) {
                    relevant += index.getWordRank();
                }
            }
            pageWithRelevance.put(page, relevant);
        }

        HashMap<Page, Float> pageWithAbsRelevance = new HashMap<>();
        for (Page page : pageWithRelevance.keySet()) {
            float absRelevant = pageWithRelevance.get(page) / Collections.max(pageWithRelevance.values());
            pageWithAbsRelevance.put(page, absRelevant);
        }
        var sortedPageByAbsRelevance = pageWithAbsRelevance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        for (Page page : sortedPageByAbsRelevance.keySet()) {
            var uri = page.getPath();
            var content = page.getContent();
            StringBuilder clearContent = new StringBuilder();
            var title = clearElements(content, fieldList.get(0).getSelector());
            var body = clearElements(content, fieldList.get(1).getSelector());
            clearContent.append(title).append(System.lineSeparator()).append(body);

            var absRelevance = sortedPageByAbsRelevance.get(page);
            var snippet = getSnippet(clearContent.toString(), searchWordsLemmaList);
//            for (Index index : findingIndexList) {
//                if (index.getPage() == page) {
//                    var lemma = index.getLemma();
//                }
//            }
            searchTextDtoList.add(new SearchTextDto(uri, title, snippet, absRelevance));
        }


        return searchTextDtoList;
    }

    private String getSnippet(String content, List<String> lemmaList) {
        List<Integer> lemmaIndex = new ArrayList<>();
        StringBuilder result = new StringBuilder();
//        String newContent = content;
        for (String lemma : lemmaList) {
            lemmaIndex.addAll(morphology.findLemmaIndex(content, lemma));
        }
//        int wordEnd = 0;
        for (Integer index : lemmaIndex) {
            int start = index;
            int end = content.indexOf(" ", start);
            String word = content.substring(start, end);
//            newContent = newContent.replaceFirst(word, "<b>" + word + "</b>");
            int lastPoint = content.lastIndexOf(" ", start);
            String text = content.substring(lastPoint, end + 50);
            text = text.replaceAll(word, "<b>" + word + "</b>");
            result.append(text).append("... ");
//            wordEnd = end;
        }
//        Pattern pattern = Pattern.compile("<b>[^\\s]+");
//        Matcher matcher = pattern.matcher(newContent);
//        int wordEnd = 0;
//        while (matcher.find()) {
//            if (matcher.start() - wordEnd > 30) {
//                int lastPoint = newContent.lastIndexOf(" ", matcher.start() - 20);
//                String text = newContent.substring(lastPoint, matcher.end() + 50);
//                result.append(text).append("... ");
//            }
//            wordEnd = matcher.end();
//        }

        return result.toString();
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
