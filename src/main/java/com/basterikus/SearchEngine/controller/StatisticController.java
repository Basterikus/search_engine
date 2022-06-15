package com.basterikus.SearchEngine.controller;

import com.basterikus.SearchEngine.dto.statistic.StatisticDto;
import com.basterikus.SearchEngine.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping("/api/statistics")
    public ResponseEntity<Object> getStatistic() {
        var statistic = statisticService.getStatistic();
        return ResponseEntity.ok(statistic);
    }
}
