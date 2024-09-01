package searchengine.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.dto.searching.SearchQuery;
import searchengine.dto.searching.SearchingResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.indexService.IndexService;
import searchengine.services.searchService.SearchService;
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
        return ResponseEntity.ok(statisticsService.getStatistics());
        /* аналог  */
 ///        StatisticsResponse statisticsResponse = statisticsService.getStatistics();
 //        return new ResponseEntity<>(statisticsResponse, HttpStatus.OK);
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<IndexingResponse> startIndexing() {
       IndexingResponse indexingResponse = indexService.startIndexing();
       return new ResponseEntity<>(indexingResponse, HttpStatus.OK);

    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<IndexingResponse> stopIndexing() {
        IndexingResponse indexingResponse = indexService.stopIndexing();
        return new ResponseEntity<>(indexingResponse, HttpStatus.OK);
    }


    @PostMapping("/indexPage")
    public ResponseEntity<IndexingResponse> indexPage(@RequestBody String page) {
        IndexingResponse indexingResponse = indexService.indexSinglePage(page);
        return new ResponseEntity<>(indexingResponse, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<SearchingResponse> search(@RequestBody SearchQuery query){
            SearchingResponse searchingResponse = searchService.search(query);
            return new ResponseEntity<>(searchingResponse, HttpStatus.OK);
    }
}
