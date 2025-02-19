package searchengine.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.dto.searching.SearchQuery;
import searchengine.dto.searching.SearchingResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.indexService.IndexService;
import searchengine.services.searchService.SearchService;
import searchengine.services.statisticService.StatisticsService;

@RestController  // этот контроллер будет работать по стандарту REST и, в частности, возвращать ответы в формате JSON
@RequestMapping("/api")  // устанавливает префикс в пути запроса: все запросы, начинающиеся с /api, будут направляться
// на методы этого контроллера
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


    // метод POST - Способ передачи данных: В теле HTTP-запроса
    @PostMapping(value = "/indexPage",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}) // из формы браузера приходит запрос K-V: \ url	"https://kemperus.ru/special_camper" \
    public ResponseEntity<IndexingResponse> indexPage(@RequestParam MultiValueMap<String,String> paramMap) { // и здесь в контроллере декодируем адрес страницы в строку
        String page = paramMap.get("url").get(0);
        IndexingResponse indexingResponse = indexService.indexSinglePage(page);
        return new ResponseEntity<>(indexingResponse, HttpStatus.OK);
    }

    @GetMapping( "/search") // метод GET - Способ передачи данных: через URL
    // про @ModelAttribute - https://sky.pro/wiki/java/peredacha-slozhnogo-obyekta-kak-get-parametra-v-spring-mvc/
    public ResponseEntity<SearchingResponse> search(@ModelAttribute SearchQuery query) {
        SearchingResponse searchingResponse = searchService.search(query);
        return new ResponseEntity<>(searchingResponse, HttpStatus.OK);
    }
}
