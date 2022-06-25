package com.basterikus.SearchEngine.controller;

import com.basterikus.SearchEngine.dto.response.FalseResponse;
import com.basterikus.SearchEngine.dto.response.TrueResponse;
import com.basterikus.SearchEngine.service.IndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class IndexController {

    private final IndexService indexService;


    @GetMapping("/startIndexing")
    public ResponseEntity<Object> startIndexingAll() {
        if (indexService.indexAll()) {
            return new ResponseEntity<>(new TrueResponse(true), HttpStatus.OK);
        } else return new ResponseEntity<>(new FalseResponse(false, "Индексация уже запущена"),
                HttpStatus.METHOD_NOT_ALLOWED);
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<Object> stopIndexing() {
        if (indexService.stopIndexing()) {
            return new ResponseEntity<>(new TrueResponse(true), HttpStatus.OK);
        } else return new ResponseEntity<>(new FalseResponse(false, "Индексация не запущена"),
                HttpStatus.METHOD_NOT_ALLOWED);
    }

    @PostMapping("/indexPage")
    public ResponseEntity<Object> startIndexingOne(String url) {
        if (indexService.indexUrl(url)) {
            return new ResponseEntity<>(new TrueResponse(true), HttpStatus.OK);
        } else return new ResponseEntity<>(new FalseResponse(false,
                "Данная страница находится за пределами сайтов"),
                HttpStatus.METHOD_NOT_ALLOWED);
    }
}
