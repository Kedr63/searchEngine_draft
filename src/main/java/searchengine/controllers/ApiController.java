package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.indexing.IndexResponse;
import searchengine.dto.indexing.IndexResponseError;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.serviceIndex.IndexService;
import searchengine.services.StatisticsService;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;

    private final IndexService indexService;

    public ApiController(StatisticsService statisticsService, IndexService indexService) {
        this.statisticsService = statisticsService;
        this.indexService = indexService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<IndexResponse> startIndexing() throws Exception{
        return ResponseEntity.ok(indexService.startIndexing());
    }

   @GetMapping("/stopIndexing")
    public ResponseEntity<IndexResponseError> stopIndexing() throws InterruptedException {
        return ResponseEntity.ok(indexService.stopIndexing());
    }

   /* @GetMapping("/throwsException")
    public ResponseEntity<IndexResponseError> throwsException() throws IOException {
        return ResponseEntity.ok(indexService.throwsException());
    }*/


}
