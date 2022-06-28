package com.basterikus.SearchEngine.parser;

import com.basterikus.SearchEngine.dto.IndexDto;
import com.basterikus.SearchEngine.model.Field;
import com.basterikus.SearchEngine.model.Lemma;
import com.basterikus.SearchEngine.model.Page;
import com.basterikus.SearchEngine.model.Site;
import com.basterikus.SearchEngine.morphology.Morphology;
import com.basterikus.SearchEngine.repository.FieldRepository;
import com.basterikus.SearchEngine.repository.LemmaRepository;
import com.basterikus.SearchEngine.repository.PageRepository;
import com.basterikus.SearchEngine.utils.ClearHtmlCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Indexing implements IndexParser {
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final FieldRepository fieldRepository;
    private final Morphology morphology;
    private List<IndexDto> indexDtoList;

    @Override
    public void run(Site site) {
        List<Page> pageList = pageRepository.findBySite(site);
        List<Lemma> lemmaList = lemmaRepository.findBySite(site);
        List<Field> fieldList = fieldRepository.findAll();
        indexDtoList = new ArrayList<>();

        for (Page page : pageList) {
            if (page.getStatusCode() == 200) {
                Integer pageId = page.getId();
                var content = page.getContent();
                var title = ClearHtmlCode.clear(content, fieldList.get(0).getSelector());
                var body = ClearHtmlCode.clear(content, fieldList.get(1).getSelector());
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
                        log.debug("Lemma not found");
                    }
                }
            } else {
                log.debug("Bad status code - " + page.getStatusCode());
            }
        }
    }

    @Override
    public List<IndexDto> getIndexList() {
        return indexDtoList;
    }
}
