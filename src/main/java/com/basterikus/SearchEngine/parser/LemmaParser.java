package com.basterikus.SearchEngine.parser;

import com.basterikus.SearchEngine.dto.IndexDto;
import com.basterikus.SearchEngine.dto.LemmaDto;
import com.basterikus.SearchEngine.model.*;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class LemmaParser implements Parser {
    private final PageRepository pageRepository;
    private final FieldRepository fieldRepository;
    private final static Set<String> serviceWords;
    private static LuceneMorphology russianMorph;
    private static LuceneMorphology englishMorph;
    private List<IndexDto> indexDtoList;
    private List<LemmaDto> lemmaDtoList;

    static {
        serviceWords = new TreeSet<>();
        serviceWords.add("ЧАСТ");
        serviceWords.add("СОЮЗ");
        serviceWords.add("ПРЕДЛ");
        serviceWords.add("МЕЖД");
        serviceWords.add("PART");
        serviceWords.add("CONJ");
        serviceWords.add("ARTICLE");
        serviceWords.add("PREP");
        try {
            russianMorph = new RussianLuceneMorphology();
            englishMorph = new EnglishLuceneMorphology();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<IndexDto> getIndexDtoList() {
        return indexDtoList;
    }

    @Override
    public List<LemmaDto> getLemmaDtoList() {
        return lemmaDtoList;
    }

    @Override
    public void parse() {
        indexDtoList = new ArrayList<>();
        lemmaDtoList = new ArrayList<>();
        List<Page> pageList = (List<Page>) pageRepository.findAll();
        List<Field> fieldList = (List<Field>) fieldRepository.findAll();
        HashMap<String, Integer> lemmaList = new HashMap<>();
        for (Page page : pageList) {
            var content = page.getContent();
            var title = clearElements(content, fieldList.get(0).getSelector());
            var body = clearElements(content, fieldList.get(1).getSelector());
            var titleList = getLemmaList(title);
            var bodyList = getLemmaList(body);

            var lemmaFrequencyList = getLemmaFrequency(titleList, bodyList);
            var lemmaIndexList = getIndexLemma(titleList, bodyList);

            Set<String> allWords = new HashSet<>();
            allWords.addAll(titleList.keySet());
            allWords.addAll(bodyList.keySet());
            for (String word : allWords) {
                int frequency = lemmaList.getOrDefault(word, 0);
                lemmaList.put(word, frequency + 1);
            }

            for (String lemma : lemmaIndexList.keySet()) {
                var rank = lemmaIndexList.get(lemma);
                indexDtoList.add(new IndexDto(page.getId(), lemma, rank));
            }
        }

        for (String lemma : lemmaList.keySet()) {
            var frequency = lemmaList.get(lemma);
            lemmaDtoList.add(new LemmaDto(lemma, frequency));
        }

    }

    private HashMap<String, Integer> getLemmaFrequency(HashMap<String, Integer> titleList,
                                                           HashMap<String, Integer> bodyList) {

        HashMap<String, Integer> lemmaList = new HashMap<>();
        Set<String> allWords = new HashSet<>();
        allWords.addAll(titleList.keySet());
        allWords.addAll(bodyList.keySet());
        for (String word : allWords) {
            int frequency = lemmaList.getOrDefault(word, 0);
            lemmaList.put(word, frequency + 1);
        }
        return lemmaList;
    }

    private HashMap<String, Float> getIndexLemma(HashMap<String, Integer> titleList,
                                                 HashMap<String, Integer> bodyList) {

        HashMap<String, Float> lemmaIndex = new HashMap<>();
        for (String lemma : titleList.keySet()) {
            float rank = Float.valueOf(titleList.get(lemma));
            lemmaIndex.put(lemma, rank);
        }

        for (String lemma : bodyList.keySet()) {
            float bodyRank = (float) (bodyList.get(lemma) * 0.8);
            float currentRank = lemmaIndex.getOrDefault(lemma, 0.0F);
            float totalRank = bodyRank + currentRank;
            lemmaIndex.put(lemma, totalRank);
        }
        return lemmaIndex;
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

    private HashMap<String, Integer> getLemmaList(String content) {
        content = content.toLowerCase(Locale.ROOT).replaceAll("\\p{Punct}|[0-9]|@|©|◄|»|«|—|-|№|…", " ");
        HashMap<String, Integer> lemmaList = new HashMap<>();
        var elements = content.split("\\s+");
        for (String el : elements) {
            System.out.println(el);
            var wordsList = getLemma(el);
            for (String word : wordsList) {
                int count = lemmaList.getOrDefault(word, 0);
                lemmaList.put(word, count + 1);
            }
        }
        return lemmaList;
    }

    private List<String> getLemma(String word) {
        List<String> lemmaList = new ArrayList<>();
        try {
            var baseRusForm = russianMorph.getNormalForms(word);
            if (!isRusServiceWord(word)) {
                lemmaList.addAll(baseRusForm);
            }
        } catch (Exception e) {
//            var baseEngForm = englishMorph.getNormalForms(word);
//            if (!isEngServiceWord(word)) {
//                lemmaList.addAll(baseEngForm);
//            }
            System.out.println("Неизвестный символ");
        }
        return lemmaList;
    }

    private boolean isRusServiceWord(String word) {
        var morphForm = russianMorph.getMorphInfo(word);
        for (String morph : morphForm) {
            var wordInfo = morph.split("\s");
            for (String info : wordInfo) {
                if (serviceWords.contains(info)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isEngServiceWord(String word) {
        var morphForm = englishMorph.getMorphInfo(word);
        for (String morph : morphForm) {
            var wordInfo = morph.split("\s");
            for (String info : wordInfo) {
                if (serviceWords.contains(info)) {
                    return true;
                }
            }
        }
        return false;
    }
}
