package com.basterikus.SearchEngine.morphology;

import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LuceneMorphology implements Morphology {
    private final static Set<String> serviceWords;
    private static org.apache.lucene.morphology.LuceneMorphology russianMorph;
    private final static String regex = "\\p{Punct}|[0-9]|@|©|◄|»|«|—|-|№|…";
//    private static org.apache.lucene.morphology.LuceneMorphology englishMorph;

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
//            englishMorph = new EnglishLuceneMorphology();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public HashMap<String, Integer> getLemmaList(String content) {
        content = content.toLowerCase(Locale.ROOT)
                .replaceAll(regex, " ");
        HashMap<String, Integer> lemmaList = new HashMap<>();
        var elements = content.split("\\s+");
        for (String el : elements) {
//            System.out.println(el);
            var wordsList = getLemma(el);
            for (String word : wordsList) {
                int count = lemmaList.getOrDefault(word, 0);
                lemmaList.put(word, count + 1);
            }
        }
        return lemmaList;
    }

    @Override
    public List<String> getLemma(String word) {
        List<String> lemmaList = new ArrayList<>();
        try {
            var baseRusForm = russianMorph.getNormalForms(word);
            if (!isServiceWord(word)) {
                lemmaList.addAll(baseRusForm);
            }
        } catch (Exception e) {
//            var baseEngForm = englishMorph.getNormalForms(word);
//            if (!isEngServiceWord(word)) {
//                lemmaList.addAll(baseEngForm);
//            }
            System.out.println("Неизвестный символ - " + word);
        }
        return lemmaList;
    }

    @Override
    public List<Integer> findLemmaIndex(String content, String lemma) {
        List<Integer> lemmaIndexList = new ArrayList<>();
        var elements = content.toLowerCase(Locale.ROOT).split("\\p{Punct}|\\s");
        int index = 0;
        for (String el : elements) {
            var lemmas = getLemma(el);
            for (String lem : lemmas) {
                if (lem.equals(lemma)) {
                    lemmaIndexList.add(index);
                }
            }
            index += el.length() + 1;
        }
        return lemmaIndexList;
    }

    private boolean isServiceWord(String word) {
        var morphForm = russianMorph.getMorphInfo(word);
        for (String morph : morphForm) {
            var wordInfo = morph.split("\\s");
            for (String info : wordInfo) {
                if (serviceWords.contains(info)) {
                    return true;
                }
            }
        }
        return false;
    }
}
