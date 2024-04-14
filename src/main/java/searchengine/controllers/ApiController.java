package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.indexing.IndexResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.model.SiteEntity;
import searchengine.services.StatisticsService;
import searchengine.services.indexService.IndexService;

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
    public ResponseEntity<IndexResponse> startIndexing() {
        return indexService.startIndexing();

    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<IndexResponse> stopIndexing() {
        return indexService.stopIndexing();
    }

   /* @GetMapping("/throwsException")
    public ResponseEntity<IndexResponseError> throwsException() throws IOException {
        return ResponseEntity.ok(indexService.throwsException());
    }*/

    @PostMapping("/indexPage")
    public ResponseEntity<IndexResponse> addOrUpdateIndexPage(@RequestBody SiteEntity siteEntity) {
        return null;
    }
}
