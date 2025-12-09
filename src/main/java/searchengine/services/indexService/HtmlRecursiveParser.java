package searchengine.services.indexService;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import searchengine.config.UserAgent;
import searchengine.dto.dtoToBD.PageDto;
import searchengine.dto.dtoToBD.SiteDto;
import searchengine.dto.indexing.PageParsed;
import searchengine.exceptions.FailedConnectionException;
import searchengine.model.StatusIndex;
import searchengine.services.PoolService;
import searchengine.services.indexService.lemmaParser.LemmaParseable;
import searchengine.services.indexService.lemmaParser.LemmaParser;
import searchengine.services.pageService.PageService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Logger;

/**
 * Пропарсит страницу URL и соберет на этой странице список {@code List<HtmlRecursiveParser> tasks}
 * новых task (url с аттрибутом {@code href} на странице, которых нет еще в БД). Собраные tasks будут запущены,
 * каждая task ассинхронно методом {@code fork}, и когда закончатся на странице tasks начнет возвращать результаты.
 * Рекурсивно будем погружаться в ссылки на странице до тех пор пока на странице не останется ссылок, которых нет в БД.
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
        if (UtilitiesIndexing.stopStartIndexingMethod) {  // if in ApiController "/stopIndexing"
            return; // останавливаем код
        }

        PageParsed pageParsed;
        PageDto pageDto;
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
            pageParsed = getParsedPage(url);
        } catch (IOException ex) {
            synchronized (UtilitiesIndexing.lockPageRepository) {
                poolService.getPageService().deletePageById(pageDto.getId()); //
            }
            getLastErrorOfException(ex);
            return; //  и остановим выполнение кода с помощью return
        }

        // если нет IOException -> заполним pageEntity остальными данными
        fillPageDtoAndSaveBD(pageDto, pageParsed);

        siteDto.setStatusTime(LocalDateTime.now());
        siteDto = poolService.getSiteService().saveSiteDto(siteDto);

        extractLemmasFromPage(pageParsed.getDoc(), pageDto, siteDto, poolService);

        if (UtilitiesIndexing.indexingSinglePage) { // при индексации отдельной страницы здесь прервем код
            return;
        }

        List<String> linksFoundOnThisPage = pageParsed.getDoc()
                .select("body")
                .select("a[href~=^((" + url + ")|(/[^A-Z#@?\\.]*))((/[^A-Z#@?\\.]*)|(/[^A-Z#@?\\.]*)\\.html)$]")
                .stream().map(element -> element.attr("href"))
                .distinct().toList();

        //📌 a[href^=/][href~=(/\w+\z|\w/\z|.html)] - в теге /а/ будет искать href начинающийся на "/", далее href с регулярным
        // выражением ("/" ноль или несколько букв, подчеркивание или цифр (\\w*) и это конец текста (\\z) | или в конце текста / (\w/\z)
        // | или в конце .html

//           Elements listOfLinksFoundOnThisPage = doc.select("a[href^=/][href~=(/\\w*\\z|\\w/\\z|.html)]")
//                    .not("[href*=#]").stream().distinct().collect(Collectors.toCollection(Elements::new));


        for (String link : linksFoundOnThisPage) {
            synchronized (UtilitiesIndexing.lockPageRepository) {
                // если такая ссылка link есть в БД, то переходим к следующему элементу цикла
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
     * @throws RuntimeException получим методом {@code .get} из {@code Future<IndexingResponse>}
     * в методе {@code getIndexingResponseListFromFutureList} класса {@code UtilitiesIndexing} примененного
     * в классе {@code IndexServiceImp}
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
        // synchronized (IndexServiceImp.lock) {
        return pageService.isPresentPageEntityWithThatPath(href, siteId);
        //  }

    }

    /**
     * Получим из URL пропарсенный HTML Document со status code,
     * если этот метод выбросит IOException, то в catch блоке удалим pageEntity,
     * который начали добавлять в БД
     *
     * @param url локальный путь в виде <i><b>/campers/turist-plus</b></i>
     */
    private PageParsed getParsedPage(String url) throws IOException {
        PageParsed pageParsed = new PageParsed();
        Document doc;
        Connection.Response response;
        int code;

        //    try {
        response = Jsoup.connect(url)
                .userAgent(generateUserAgent())
                .referrer("https://www.google.com")
                .ignoreHttpErrors(true)
                //  .ignoreContentType(true)
                .followRedirects(true)
                .timeout(60000)
                .execute();
//        } catch (HttpStatusException e) {
//            throw new HttpStatusException(e.getMessage(), e.getStatusCode(), url);
//        } catch (IOException e) {
//            throw new IOException("Проблема с соединением", e.getCause());
//            //  throw new FailedConnectionException(e.getMessage() + " response пустой");
//        }
        try {
            Thread.sleep(generateRandomRangeDelay()); // задержка между запросами
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        code = response.statusCode();

        if (code == 200) {
            doc = response.parse();
        } else {
            doc = new Document(url);
            // String errorMessage = response.statusMessage();
            // code = response.map(Connection.Response::statusCode).orElse(404);
            Logger.getLogger(HtmlRecursiveParser.class.getName()).info("ошибка HttpErrors в: " + url + " code " + code);
            // documentParsed.setErrorMessage(response.statusMessage());
        }
        pageParsed.setDoc(doc);
        pageParsed.setCode(code);
        //  documentParsed = new DocumentParsed(doc, code);
        return pageParsed;
    }


    private void fillPageDtoAndSaveBD(PageDto pageDto, PageParsed pageParsed) {
        pageDto.setCode(pageParsed.getCode());
        //  Elements contentPage = documentParsed.getDoc().select("body"); // get all content of the page from tag <body>
        Document contentPage = pageParsed.getDoc();
//        Elements elements = contentPage.select("body");
//        String t = "" + elements;

        String contentViaString = "" + contentPage;
        String cleanContent = contentViaString.replaceAll("[\\p{So}\\p{Cn}]", " "); // очистим String от смайликов в тексте (https://sky.pro/wiki/java/udalenie-emodzi-i-znakov-iz-strok-na-java-reshenie/)
        pageDto.setContent(cleanContent);
        PageService pageService = poolService.getPageService();
        //   synchronized (UtilitiesIndexing.lockPageRepository) {
        PageDto savedPageDto = pageService.savePageDto(pageDto); // обновим сущ-ую запись в БД
        //  }
    }

    private String generateUserAgent() {
        List<UserAgent> userAgents = poolService.getUserAgentList().getUserAgents();
        Map<Integer, UserAgent> nameMap = new LinkedHashMap<>();
        String name = "";

        int number = 1;
        for (UserAgent usr : userAgents) {
            nameMap.put(number, usr);
            number++;
        }

        int randomNumber = 1 + (int) (Math.random() * nameMap.size());
//        for (Integer integer : nameMap.keySet()) {
//            if (integer == randomNumber) {
//                name = nameMap.get(integer).getName();
//            }
//        }
        name = nameMap.get(randomNumber).getName();
        return name;
    }

    private long generateRandomRangeDelay() {
        long beginningOfRange = 500;
        return (long) (beginningOfRange + (Math.random() * 4500));
    }


//    private void updateSiteEntity(SiteEntity siteEntity, DocumentParsed documentParsed) {
//        if (documentParsed.getCode() != 200) {
//            String messageError = String.valueOf(HttpStatus.resolve(documentParsed.getCode()));
//            siteEntity.setLastError(messageError);
//        }
//        siteEntity.setStatusTime(LocalDateTime.now());
//        poolService.getSiteService().saveSiteEntity(siteEntity);
//    }


    /*private void searchLemmasInPage(PageEntity pageEntity, SiteEntity siteEntity, PoolService poolService) {
        if (pageEntity.getCode() == 200) {
            try {
                LemmaParser lemmaParser = new LemmaParser(poolService);
                Map<String, Integer> mapLemma = lemmaParser.getLemmaFromContentPage(pageEntity.getContent());
                lemmaParser.getLemmaEntitiesAndSaveBD(siteEntity, pageEntity, mapLemma);
            } catch (IOException | NullPointerException e) {
                Logger.getLogger(HtmlParser.class.getName()).info("catch IOEx lemma - " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }*/

    /**
     * Извлекает леммы со страницы в виде K-V, где K - лемма, V - количество леммы на странице */
    private void extractLemmasFromPage(Document document, PageDto pageDto, SiteDto siteDto, PoolService poolService) {
        if (pageDto.getCode() == 200) {
            try {
                LemmaParseable lemmaParser = new LemmaParser(poolService);
                Map<String, Integer> lemmasCountsMap = lemmaParser.getLemmaWordToAmountOnPageMapFromContent(document);
                lemmaParser.getLemmaDtoAndIndexDto(siteDto, pageDto, lemmasCountsMap);
            } catch (IOException | NullPointerException e) {
                saveLastErrorInSiteEntity(e);
                // throw new RuntimeException(e.getMessage(), e.getCause());
                if (e instanceof IOException) {
                    throw new FailedConnectionException(((IOException) e).getMessage());
                } else {
                    throw new IllegalArgumentException(((NullPointerException) e).getMessage(), ((NullPointerException) e).getCause());
                }
            }
        }
    }
}
