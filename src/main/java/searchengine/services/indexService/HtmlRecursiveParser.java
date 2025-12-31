package searchengine.services.indexService;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.nodes.Document;
import searchengine.dto.dtoToBD.PageDto;
import searchengine.dto.dtoToBD.SiteDto;
import searchengine.dto.indexing.PageParsed;
import searchengine.model.StatusIndex;
import searchengine.services.PoolService;
import searchengine.services.indexService.lemmaParser.LemmaParseable;
import searchengine.services.indexService.lemmaParser.LemmaParserImpl;
import searchengine.services.indexService.pageParser.PageParseable;
import searchengine.services.indexService.pageParser.PageParserJSOUPImpl;
import searchengine.services.pageService.PageService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;

/**
 * Пропарсит страницу {@code URL} и соберет на этой странице список {@code List<HtmlRecursiveParser> tasks} -
 * новых <b><i>task</i></b> (url с аттрибутом {@code href} на странице, которых нет еще в таблице {@code page} БД ).
 * Собраные <b><i>tasks</i></b> будут запущены, каждая <b><i>task</i></b> ассинхронно методом {@code fork}, и когда
 * закончатся на странице <b><i>tasks</i></b> - начнет возвращать результаты.
 * Рекурсивно будем погружаться в ссылки на странице до тех пор, пока на странице не останется ссылок, которых нет в БД.
 */
@Getter
@Setter
public class HtmlRecursiveParser extends RecursiveAction {
    private String url;
    private SiteDto siteDto;
    private PoolService poolService;

    public HtmlRecursiveParser() {
    }

    public HtmlRecursiveParser(String url, SiteDto siteDto, PoolService poolService) {
        this.url = url;
        this.siteDto = siteDto;
        this.poolService = poolService;
    }

    @Override
    protected void compute() {
        if (UtilitiesIndexing.stopIndexing) {
            return; // останавливаем код
        }

        PageParsed pageParsed;
        PageDto pageDto;
        PageParseable pageParser = new PageParserJSOUPImpl(poolService);
        List<HtmlRecursiveParser> tasks = new ArrayList<>();

        String localAddressUrl = extractLocalAddressUrl(url, siteDto);

        synchronized (UtilitiesIndexing.lockPageRepository) {
            if (!isPresentPathInPageRepository(localAddressUrl, siteDto.getId(), poolService.getPageService())) {
                pageDto = new PageDto();
                pageDto.setPath(localAddressUrl);
                pageDto.setContent(""); // пока вставим заглушку, чтоб долго не удерживать \lockPageRepository\
                pageDto.setSiteId(siteDto.getId());
                pageDto = poolService.getPageService().savePageDto(pageDto);
            } else {
                return;  // если path есть в базе, то останавливаем здесь код
            }
        }

        try {
            pageParsed = pageParser.getParsedPage(url);
        } catch (IOException ex) {
            synchronized (UtilitiesIndexing.lockPageRepository) {
                poolService.getPageService().deletePageById(pageDto.getId()); //
            }
            getLastErrorOfException(ex);
            return; //  и остановим выполнение кода с помощью return
        }

        fillPageDtoAndSaveBD(pageDto, pageParsed);

        siteDto.setStatusTime(LocalDateTime.now());
        siteDto = poolService.getSiteService().saveSiteDto(siteDto);

        extractLemmasFromPage(pageParsed.getDoc(), pageDto, siteDto, poolService);

        if (UtilitiesIndexing.indexingSinglePage) { // при индексации отдельной страницы здесь прервем код
            return;
        }

        List<String> linksFoundOnThisPage = getLinksFoundOnThisPage(pageParsed);

        for (String link : linksFoundOnThisPage) {
            synchronized (UtilitiesIndexing.lockPageRepository) {
                if (isPresentPathInPageRepository(extractLocalAddressUrl(link, siteDto), siteDto.getId(), poolService.getPageService())) {
                    continue;
                }
            }
            String fullHref = siteDto.getUrl() + extractLocalAddressUrl(link, siteDto);
            HtmlRecursiveParser task = new HtmlRecursiveParser(fullHref, siteDto, poolService);

            task.fork();
            tasks.add(task);
        }

        if (!tasks.isEmpty()) {
            for (HtmlRecursiveParser task : tasks) {
                task.join();
            }
        }
    }

    /**
     * Найдет на пропарсенной (document) странице ссылки на другие страницы и сохранит в список оригинальных ссылок
     * без повторов.
     *
     * @param pageParsed пропарсенная страница в виде {@code Document} со статусом кода ответа
     * @return Список ссылок на другие страницы сайта или пустой список
     * @note В теге {@code а} будет искать {@code href} с регулярным выражением.
     * <a href="https://regex101.com/r/V72zJG/7"> Пример как работает это Regex</a>
     */
    private List<String> getLinksFoundOnThisPage(PageParsed pageParsed) {
        return pageParsed.getDoc()
                .select("body")
                .select("a[href~=^((" + url + ")|(/[^A-Z#@?\\.]*))((/[^%A-Z#@?\\.]*)|(/[^A-Z#@?\\.]*)\\.html)$]")
                .stream().map(element -> element.attr("href"))
                .distinct().toList();
    }

    private String extractLocalAddressUrl(String url, SiteDto siteDto) {
        String localAddressUrl = "";
        String urlServer = siteDto.getUrl();
        localAddressUrl = url.replace(urlServer, "");
        if (localAddressUrl.endsWith("/")) {
            localAddressUrl = localAddressUrl.substring(0, localAddressUrl.length() - 1);
        }
        if (localAddressUrl.isEmpty()) {
            localAddressUrl = "/";
        }
        return localAddressUrl;
    }

    /**
     * сохранит ошибку в таблицу БД site и бросит исключение
     *
     * @throws RuntimeException получим методом {@code .get} из {@code Future<IndexingResponse>}
     *                          в методе {@code getIndexingResponseListFromFutureList} класса {@code UtilitiesIndexing} примененного
     *                          в классе {@code IndexServiceImp}
     */
    private void getLastErrorOfException(Exception ex) {
        saveLastErrorInSiteEntity(ex);
        throw new RuntimeException(ex);
    }

    private void saveLastErrorInSiteEntity(Exception ex) {
        siteDto.setLastError(ex.getClass() + " - " + ex.getMessage() + " - сайт - " + siteDto.getUrl());
        siteDto.setStatusIndex(StatusIndex.FAILED);
        poolService.getSiteService().saveSiteDto(siteDto);
    }


    private boolean isPresentPathInPageRepository(String href, int siteId, PageService pageService) {
        return pageService.isPresentPageEntityWithThatPath(href, siteId);
    }

    /**
     * Если не было IOException, то заполним pageDto остальными данными.
     * @Note: <p>{@code String cleanContent = contentViaString.replaceAll("[\\p{So}\\p{Cn}]", " ");}
     * Этим кодом <a href="https://sky.pro/wiki/java/udalenie-emodzi-i-znakov-iz-strok-na-java-reshenie/">очистим String от смайликов в тексте</a></p>
     */
    private void fillPageDtoAndSaveBD(PageDto pageDto, PageParsed pageParsed) {
        pageDto.setCode(pageParsed.getCode());
        Document contentPage = pageParsed.getDoc();
        String contentViaString = "" + contentPage;
        String cleanContent = contentViaString.replaceAll("[\\p{So}\\p{Cn}]", " ");
        pageDto.setContent(cleanContent);
        poolService.getPageService().savePageDto(pageDto); // обновим сущ-ую запись в БД
    }

    /**
     * Извлекает леммы со страницы в виде K-V, где K - лемма, V - количество леммы на странице
     */
    private void extractLemmasFromPage(Document document, PageDto pageDto, SiteDto siteDto, PoolService poolService) {
        if (pageDto.getCode() == 200) {
            try {
                LemmaParseable lemmaParser = new LemmaParserImpl(poolService);
                Map<String, Integer> lemmasCountsMap = lemmaParser.getLemmaToAmountOnPageMapFromContentOfDocument(document);
                lemmaParser.getLemmaDtoIndexDto(siteDto, pageDto, lemmasCountsMap);
            } catch (IOException e) {
                saveLastErrorInSiteEntity(e);
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
        }
    }
}
