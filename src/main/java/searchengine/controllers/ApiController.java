package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.indexing.IndexResponse;
import searchengine.dto.searching.SearchResult;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.searchService.SearchService;
import searchengine.services.indexService.IndexService;
import searchengine.services.statisticService.StatisticsService;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;

    private final IndexService indexService;

    private final SearchService searchService;


    public ApiController(StatisticsService statisticsService, IndexService indexService, SearchService searchService) {
        this.statisticsService = statisticsService;
        this.indexService = indexService;
        this.searchService = searchService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
       // return ResponseEntity.ok(statisticsService.getStatistics());
        return statisticsService.getStatistics();
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<IndexResponse> startIndexing() {
        return indexService.startIndexing();

    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<IndexResponse> stopIndexing() {
        return indexService.stopIndexing();
    }


    @PostMapping("/indexPage")
    public ResponseEntity<IndexResponse> indexPage(@RequestBody String page) {
        return indexService.indexPage(page);
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResult> search(@RequestBody String query){
            return searchService.search(query);
    }
}
