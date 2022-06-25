package com.basterikus.SearchEngine.service.impl;

import com.basterikus.SearchEngine.dto.SearchDto;
import com.basterikus.SearchEngine.model.Index;
import com.basterikus.SearchEngine.model.Lemma;
import com.basterikus.SearchEngine.model.Page;
import com.basterikus.SearchEngine.model.Site;
import com.basterikus.SearchEngine.morphology.Morphology;
import com.basterikus.SearchEngine.repository.*;
import com.basterikus.SearchEngine.service.SearchService;
import com.basterikus.SearchEngine.utils.ClearHtmlCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {
    private final Morphology morphology;
    private final LemmaRepository lemmaRepository;
    private final FieldRepository fieldRepository;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final IndexRepository indexRepository;

    @Override
    public List<SearchDto> allSiteSearch(String searchText, int offset, int limit) {
        var siteList = siteRepository.findAll();
        List<SearchDto> searchData = new ArrayList<>();
        List<SearchDto> result = new ArrayList<>();
        for (Site site : siteList) {
            var dataList = siteSearch(searchText, site.getUrl(), offset, limit);
            searchData.addAll(dataList);
        }
        searchData.sort((o1, o2) -> Float.compare(o2.getRelevance(), o1.getRelevance()));
        if (searchData.size() > limit) {
            for (int i = offset; i < limit; i++) {
                result.add(searchData.get(i));
            }
            return result;
        }
        return searchData;
    }

    @Override
    public List<SearchDto> siteSearch(String searchText, String url, int offset, int limit) {
        var site = siteRepository.findByUrl(url);
        var elements = searchText.toLowerCase(Locale.ROOT).split("\\s+");
        var textLemmaList = getLemmaFromText(elements);
        var foundLemmaList = getLemmaListFromSite(textLemmaList, site);
        return getSearchDtoList(foundLemmaList, textLemmaList, offset, limit);
    }

    private List<SearchDto> getSearchDtoList(List<Lemma> lemmaList,
                                             List<String> textLemmaList,
                                             int offset,
                                             int limit) {
        List<SearchDto> result = new ArrayList<>();
        if (lemmaList.size() >= textLemmaList.size()) {
            var foundPageList = getPageList(lemmaList);
            var foundIndexList = indexRepository.findByPagesAndLemmas(lemmaList, foundPageList);
            var sortedPageByAbsRelevance = getPageAbsRelevance(foundPageList, foundIndexList);
            var dataList = getSearchData(sortedPageByAbsRelevance, textLemmaList, offset);
            if (dataList.size() > limit) {
                for (int i = offset; i < limit; i++) {
                    result.add(dataList.get(i));
                }
                return result;
            } else return dataList;
        } else return result;
    }

    private String getSnippet(String content, List<String> lemmaList) {
        List<Integer> lemmaIndex = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        for (String lemma : lemmaList) {
            lemmaIndex.addAll(morphology.findLemmaIndexInText(content, lemma));
        }
        if (lemmaIndex.size() > 5) {
            for (int i = 0; i <= 5; i++) {
                var text = getSearchWordFromContent(lemmaIndex.get(i), content);
                result.append(text).append("... ");
            }
        } else {
            for (Integer index : lemmaIndex) {
                var text = getSearchWordFromContent(index, content);
                result.append(text).append("... ");
            }
        }
        return result.toString();
    }

    private String getSearchWordFromContent(Integer index, String content) {
        int start = index;
        int end = content.indexOf(" ", start);
        String word = content.substring(start, end);
        int prevPoint;
        int lastPoint;
        if (content.lastIndexOf(" ", start) != -1) {
            prevPoint = content.lastIndexOf(" ", start);
        } else prevPoint = start;
        if (content.indexOf(" ", end + 30) != -1) {
            lastPoint = content.indexOf(" ", end + 30);
        } else lastPoint = content.indexOf(" ", end);
        String text = content.substring(prevPoint, lastPoint);
        try {
            text = text.replaceAll(word, "<b>" + word + "</b>");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return text;
    }

    private List<Lemma> getLemmaListFromSite(List<String> lemmas, Site site) {
        var lemmaList = lemmaRepository.findLemmaListBySite(lemmas, site);
        List<Lemma> result = new ArrayList<>(lemmaList);
        result.sort(Comparator.comparingInt(Lemma::getFrequency));
        return result;
    }

    private List<Page> getPageList(List<Lemma> lemmaList) {
        if (lemmaList.size() > 1) {
            return pageRepository.findByLemmaList(lemmaList);
        } else {
            return pageRepository.findByLemma(lemmaList);
        }
    }

    private List<String> getLemmaFromText(String[] elements) {
        List<String> lemmas = new ArrayList<>();
        for (String el : elements) {
            var lemmaWord = morphology.getLemma(el);
            lemmas.addAll(lemmaWord);
        }
        return lemmas;
    }

    private LinkedHashMap<Page, Float> getPageAbsRelevance(List<Page> pageList, List<Index> indexList) {
        HashMap<Page, Float> pageWithRelevance = new HashMap<>();
        for (Page page : pageList) {
            float relevant = 0;
            for (Index index : indexList) {
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
        return pageWithAbsRelevance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    private List<SearchDto> getSearchData(LinkedHashMap<Page, Float> pageList,
                                          List<String> textLemmaList,
                                          int offset) {
        List<SearchDto> result = new ArrayList<>();
        var fieldList = fieldRepository.findAll();
        for (Page page : pageList.keySet()) {
            var uri = page.getPath();
            var content = page.getContent();
            var pageSite = page.getSite();
            var site = pageSite.getUrl();
            var siteName = pageSite.getName();
            var absRelevance = pageList.get(page);


            StringBuilder clearContent = new StringBuilder();
            var title = ClearHtmlCode.clear(content, fieldList.get(0).getSelector());
            var body = ClearHtmlCode.clear(content, fieldList.get(1).getSelector());
            clearContent.append(title).append(" ").append(body);
            var snippet = getSnippet(clearContent.toString(), textLemmaList);

            result.add(new SearchDto(site,
                    siteName,
                    uri,
                    title,
                    snippet,
                    absRelevance));
        }
        return result;
    }
}