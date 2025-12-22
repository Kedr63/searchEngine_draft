package searchengine.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.dtoToBD.PageDtoSingle;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.dto.searching.SearchQuery;
import searchengine.dto.searching.SearchingResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.indexService.IndexService;
import searchengine.services.searchService.SearchService;
import searchengine.services.statisticService.StatisticsService;

/**
 * @RestController ApiController класс с аннотациями:
 * @aboutAnnotation: @RestController = @Controller + @ResponseBody. Аннотация @Controller умеет слушать, получать и отвечать на запросы.
 * раньше в Spring MVC нужно было добавлять аннотацию @ResponseBody к каждому методу, чтобы возвратить объект напрямую клиенту,
 * теперь эта необходимость отпала благодаря аннотации @RestController
 * @aboutAnnotation: @RestController  этот контроллер будет работать по стандарту REST и, в частности, возвращать ответы в формате JSON
 * @aboutAnnotation: @RequestMapping("/api")  устанавливает префикс в пути запроса: все запросы, начинающиеся с /api, будут направляться
 * на методы этого контроллера
 * @Note
 * <p>ResponseEntity<>: обобщённый класс (generic class), предназначенный для представления полного HTTP-ответа.
 * Внутри угловых скобок указывается тип возвращаемого тела ответа. Например, если мы хотим вернуть JSON, внутри скобок
 * будет указан соответствующий Java-класс, представляющий этот JSON.</p>
 */
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

    /**
     * @param pageDtoSingle pageDtoSingle представляет собой объект DTO (Data Transfer Object), который используется
     *                      для переноса данных из формы.
     *                      Из формы браузера приходит запрос от пользователя в виде K-V:
     *                      {@code "url" - "https://kemperus.ru/special_camper"}
     * @aboutAnnotation: @PostMapping аннотация означает, что метод контроллера будет обслуживать POST-запросы,
     * направленные на указанный URL "/indexPage"
     * <p>📌 метод POST - Способ передачи данных: в теле HTTP-запроса (не отображаются в строке браузера)</p>
     * <p>Метод HTTP POST предназначен для отправки данных на сервер с целью изменения состояния ресурса или
     * создания новых ресурсов</p>
     * @aboutAnnotation: @ModelAttribute создает экземпляр указанного класса (PageDtoSingle) и заполнят
     * его поля значениями из запроса.
     */
    @PostMapping(value = "/indexPage")
    public ResponseEntity<IndexingResponse> indexPage(@ModelAttribute PageDtoSingle pageDtoSingle) {
        IndexingResponse indexingResponse = indexService.indexSinglePage(pageDtoSingle);
        return new ResponseEntity<>(indexingResponse, HttpStatus.OK);
    }

    /**
     * @param query Spring возьмёт каждое свойство из URL-параметров и присвоит соответствующие значения
     *              полям объекта SearchQuery
     * @aboutAnnotation {@code @ModelAttribute} даёт возможность создать экземпляр указанного класса и заполнить
     * его поля значениями из запроса. Это удобно для ситуаций, когда вам нужно передать
     * несколько полей одновременно, упакованных в единый объект (обычно DTO)
     * <p>Дополнительная информация:  <a href="https://sky.pro/wiki/java/peredacha-slozhnogo-obyekta-kak-get-parametra-v-spring-mvc/"> Про @ModelAttribute</a></p>
     *
     */
    @GetMapping("/search")
    public ResponseEntity<SearchingResponse> search(@ModelAttribute SearchQuery query) {
        SearchingResponse searchingResponse = searchService.search(query);
        return new ResponseEntity<>(searchingResponse, HttpStatus.OK);
    }
}

