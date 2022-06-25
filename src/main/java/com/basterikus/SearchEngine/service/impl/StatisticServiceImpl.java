package com.basterikus.SearchEngine.service.impl;

import com.basterikus.SearchEngine.dto.statistic.Detailed;
import com.basterikus.SearchEngine.dto.statistic.Statistics;
import com.basterikus.SearchEngine.dto.statistic.Total;
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
    public Statistics getStatistic() {
        var total = getTotal();
        var detailed = getDetailedList();
        return new Statistics(total, detailed);
    }

    private Total getTotal() {
        var sites = siteRepository.count();
        var pages = pageRepository.count();
        var lemmas = lemmaRepository.count();
        return new Total(sites, pages, lemmas, true);
    }

    private Detailed getDetailed(Site site) {
        var url = site.getUrl();
        var name = site.getName();
        var status = site.getStatus();
        var statusTime = site.getStatusTime();
        var error = site.getLastError();
        var pages = pageRepository.countBySite(site);
        var lemmas = lemmaRepository.countBySite(site);
        return new Detailed(url, name, status, statusTime, error, pages, lemmas);
    }

    private List<Detailed> getDetailedList() {
        var siteList = siteRepository.findAll();
        List<Detailed> result = new ArrayList<>();
        for (Site site : siteList) {
            Detailed detailed = getDetailed(site);
            result.add(detailed);
        }
        return result;
    }
}
