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

// @RestController = @Controller + @ResponseBody. Аннотация @Controller умеет слушать, получать и отвечать на запросы.
// Аннотация @ResponseBody  дает фреймворку понять, что объект, который вы вернули из метода надо прогнать через HttpMessageConverter,
// чтобы получить готовое к отправке клиенту представление
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

    /**
     * @return аналог кода ниже в теле метода
     * <p>
     * StatisticsResponse statisticsResponse = statisticsService.getStatistics();
     * </p>
     * <p>
     * return new ResponseEntity<>(statisticsResponse, HttpStatus.OK);
     * </p>
     */
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
     * @param paramMap из формы браузера приходит запрос от пользователя в виде K-V: "url" - "https://kemperus.ru/special_camper"
     * и здесь в контроллере декодируем адрес страницы сайта в строку
     * <p>
     * 📌 метод POST - Способ передачи данных: В теле HTTP-запроса
     * </p>
     * <p>
     * 📌 Метод indexPage() будет вызван, когда поступает POST-запрос на путь /api/indexPage,
     * а тело запроса (@RequestParam) будет передано в качестве аргумента paramMap.
     * Причем тело запроса должно быть закодировано как форма (application/x-www-form-urlencoded): key=value&anotherKey=anotherValue
     * </p>
     * <p>
     * 📌 пример тела запроса /x-www-form-urlencoded/
     * "url" - "https://kemperus.ru/special_camper"
     * </p>
     * */

//    @PostMapping(value = "/indexPage",
//            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
//    public ResponseEntity<IndexingResponse> indexPage(@RequestParam MultiValueMap<String,String> paramMap) {
//        String page = paramMap.get("url").get(0);
//        IndexingResponse indexingResponse = indexService.indexSinglePage(page);
//        return new ResponseEntity<>(indexingResponse, HttpStatus.OK);
//    }

    /**
     * @param pageDtoSingle pageDtoSingle представляет собой объект DTO (Data Transfer Object), который используется
     *                      для переноса данных из формы.
     *                      Из формы браузера приходит запрос от пользователя в виде K-V:
     *                      <p>"url" - "https://kemperus.ru/special_camper"</p>
     *                      <p>
     * @aboutAnnotation @PostMapping аннотация означает, что метод контроллера будет обслуживать POST-запросы,
     * направленные на указанный URL "/indexPage"
     * <p>📌 метод POST - Способ передачи данных: в теле HTTP-запроса</p>
     * </p>
     * <p>
     * @aboutAnnotation @ModelAttribute создает экземпляр указанного класса (PageDtoSingle) и заполнят
     * его поля значениями из запроса.
     * </p>
     */
    @PostMapping(value = "/indexPage")
    public ResponseEntity<IndexingResponse> indexPage(@ModelAttribute PageDtoSingle pageDtoSingle) {
        String page = pageDtoSingle.getUrl();
        IndexingResponse indexingResponse = indexService.indexSinglePage(page);
        return new ResponseEntity<>(indexingResponse, HttpStatus.OK);
    }

    /**
     * @param query Spring возьмёт каждое свойство из URL-параметров и присвоит соответствующие значения
     *              полям объекта SearchQuery
     *              <p>
     * @aboutAnnotation @ModelAttribute даёт возможность создать экземпляр указанного класса и заполнить
     * его поля значениями из запроса. Это удобно для ситуаций, когда вам нужно передать
     * несколько полей одновременно, упакованных в единый объект (обычно DTO)
     * </p>
     */
    @GetMapping("/search") // метод GET - Способ передачи данных: через URL
    // про @ModelAttribute - https://sky.pro/wiki/java/peredacha-slozhnogo-obyekta-kak-get-parametra-v-spring-mvc/
    public ResponseEntity<SearchingResponse> search(@ModelAttribute SearchQuery query) {
        SearchingResponse searchingResponse = searchService.search(query);
        return new ResponseEntity<>(searchingResponse, HttpStatus.OK);
    }
}
