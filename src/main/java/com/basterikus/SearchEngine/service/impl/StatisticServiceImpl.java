package com.basterikus.SearchEngine.service.impl;

import com.basterikus.SearchEngine.dto.statistic.DetailedDto;
import com.basterikus.SearchEngine.dto.statistic.StatisticDto;
import com.basterikus.SearchEngine.dto.statistic.TotalDto;
import com.basterikus.SearchEngine.model.Site;
import com.basterikus.SearchEngine.repository.LemmaRepository;
import com.basterikus.SearchEngine.repository.PageRepository;
import com.basterikus.SearchEngine.repository.SiteRepository;
import com.basterikus.SearchEngine.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;

    @Override
    public StatisticDto getStatistic() {
        var total = getTotal();
        var detailed = getDetailedList();
        return new StatisticDto(total, detailed);
    }

    private TotalDto getTotal() {
        var sites = siteRepository.count();
        var pages = pageRepository.count();
        var lemmas = lemmaRepository.count();
        return new TotalDto(sites, pages, lemmas, true);
    }

    private DetailedDto getDetailed(Site site) {
        var url = site.getUrl();
        var name = site.getName();
        var status = site.getStatus();
        var statusTime = site.getStatusTime();
        var error = site.getLastError();
        var pages = pageRepository.countBySite(site);
        var lemmas = lemmaRepository.countBySite(site);
        return new DetailedDto(url, name, status, statusTime, error, pages, lemmas);
    }

    private List<DetailedDto> getDetailedList() {
        var siteList = siteRepository.findAll();
        List<DetailedDto> result = new ArrayList<>();
        for (Site site : siteList) {
            DetailedDto detailedDto = getDetailed(site);
            result.add(detailedDto);
        }
        return result;
    }
}
