package searchengine.services.indexService;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import searchengine.config.UserAgent;
import searchengine.dto.dtoToBD.PageDto;
import searchengine.dto.dtoToBD.SiteDto;
import searchengine.dto.indexing.DocumentParsed;
import searchengine.exceptions.FailedConnectionException;
import searchengine.model.StatusIndex;
import searchengine.services.PoolService;
import searchengine.services.pageService.PageService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Logger;


@Getter
@Setter
public class HtmlParser extends RecursiveAction {

    private String url;
    private SiteDto siteDto;
    private PoolService poolService;

    public HtmlParser() {
    }

    public HtmlParser(String url, SiteDto siteDto, PoolService poolService) {
        this.url = url;
        this.siteDto = siteDto;
        this.poolService = poolService;
    }

    @Override
    protected void compute() {

        if (UtilitiesIndexing.stopStartIndexingMethod) {  // if in ApiController "/stopIndexing"
            Logger.getLogger(HtmlParser.class.getName()).info("делаем стоп потоки if (StatusThreadsRun.threadsStopping   +  return");
            return; // останавливаем код
        }

        DocumentParsed documentParsed;
        PageDto pageDto;
        List<HtmlParser> tasks = new ArrayList<>();

        String localAddressUrl = extractLocalAddressUrl(url, siteDto);

        synchronized (UtilitiesIndexing.lockPageRepository) {
            if (!isPresentPathInPageRepository(localAddressUrl, siteDto.getId(), poolService.getPageService())) {
                pageDto = new PageDto(); // экспериментирую вместо \new PageDto()\
                pageDto.setPath(localAddressUrl);
                pageDto.setContent(""); // пока вставим заглушку, чтоб долго не удерживать \lockPageRepository\
                pageDto.setSiteId(siteDto.getId());
                pageDto = poolService.getPageService().savePageDto(pageDto);

                /* почему то с MapperModel не работает */
//                pageDto = PageDto.builder()
//                        .path(localAddressUrl)
//                        .content("")   // пока вставим заглушку, чтоб долго не удерживать \lockPageRepository\
//                        .siteId(siteDto.getId())
//                        .build(); // экспериментирую вместо \new PageDto()\
//                pageDto = poolService.getPageService().savePageDto(pageDto);
//
//                    PageEntity pageEntity = createPageEntity(linkLocate, documentParsed, siteEntity);
                Logger.getLogger(HtmlParser.class.getName()).info("save path in repository:  - " + url);

            } else {
                return;  // иначе если path есть в базе, то останавливаем здесь код
            }
        }

        try {
            documentParsed = getParsedDocument(url); // если этот метод выбросит IOException, то в catch блоке удалим pageEntity,
            // который начали добавлять в БД
        } catch (IOException ex) {
            Logger.getLogger(HtmlParser.class.getName()).info("catch IOEx " + ex.getClass() + " ex ");

            synchronized (UtilitiesIndexing.lockPageRepository) {
                Logger.getLogger(HtmlParser.class.getName()).info("deletePageEntity(pageEntity.getId() = " + pageDto.getId() + "  " + siteDto.getName());
                poolService.getPageService().deletePageById(pageDto.getId()); //
            }
            getLastErrorOfException(ex); // из этого метода выбросится \new RuntimeException(ex.getMessage(), ex.getCause())\;
            // а если ловили исключение из-за Http, то выполним метод и остановим код с помощью return
            return;
        }

        // если нет IOException -> заполним pageEntity остальными данными
        fillPageDtoAndSaveBD(pageDto, documentParsed);

        siteDto.setStatusTime(LocalDateTime.now());
        siteDto = poolService.getSiteService().saveSiteDto(siteDto);

        getLemmasFromPage(documentParsed.getDoc(), pageDto, siteDto, poolService);

        if (UtilitiesIndexing.computeIndexingSinglePage) { // при индексации отдельной страницы здесь прервем код
            return;
        }

        List<String> listOfLinksFoundOnThisPage = documentParsed.getDoc()
                .select("body")
                .select("a[href~=^((" + url + ")|(/[^A-Z#@?\\.]*))((/[^A-Z#@?\\.]*)|(/[^A-Z#@?\\.]*)\\.html)$]")
                .stream().map(element -> element.attr("href"))
                .distinct().toList();

        //📌 a[href^=/][href~=(/\w+\z|\w/\z|.html)] - в теге /а/ будет искать href начинающийся на "/", далее href с регулярным
        // выражением ("/" ноль или несколько букв, подчеркивание или цифр (\\w*) и это конец текста (\\z) | или в конце текста / (\w/\z)
        // | или в конце .html

//           Elements listOfLinksFoundOnThisPage = doc.select("a[href^=/][href~=(/\\w*\\z|\\w/\\z|.html)]")
//                    .not("[href*=#]").stream().distinct().collect(Collectors.toCollection(Elements::new));


        for (String link : listOfLinksFoundOnThisPage) {
            synchronized (UtilitiesIndexing.lockPageRepository) {
                // если такая ссылка link есть в БД, то переходим к следующему элементу цикла
                if (isPresentPathInPageRepository(extractLocalAddressUrl(link, siteDto), siteDto.getId(), poolService.getPageService())) {
                    continue;
                }
            }

            String fullHref = siteDto.getUrl() + extractLocalAddressUrl(link, siteDto);
            HtmlParser task = new HtmlParser(fullHref, siteDto, poolService);

            task.fork();
            tasks.add(task);
        }


        if (!tasks.isEmpty()) {
            for (HtmlParser task : tasks) {
                task.join();
                Logger.getLogger(HtmlParser.class.getName()).info("task.join()");
            }
//            tasks.clear();
//            Logger.getLogger(HtmlParser.class.getName()).info("tasks.clear()");
        }
//        else {
//            Logger.getLogger(HtmlParser.class.getName()).info("tasks.isEmpty()  - список задач пуст 📌");
//        }
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

    private void getLastErrorOfException(Exception ex) {

//        if (ex.getClass()== HttpStatusException.class){
//            saveLastErrorInSiteEntity(ex);
//            Logger.getLogger(HtmlParser.class.getName()).info("1 before throws : throw new HttpFailedConnectionException(ex.getMessage(), ((HttpStatusException) ex).getStatusCode())");
//
//        } else {
        //  Logger.getLogger(HtmlParser.class.getName()).info("1 before throws : " + ex.getCause().getMessage());
        saveLastErrorInSiteEntity(ex);
        //  Logger.getLogger(HtmlParser.class.getName()).info("2 before throws : " + ex.getMessage());
        throw new RuntimeException(ex);

        //    }
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


    private DocumentParsed getParsedDocument(String url) throws IOException {
        DocumentParsed documentParsed = new DocumentParsed();
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
            Logger.getLogger(HtmlParser.class.getName()).info("ошибка HttpErrors в: " + url + " code " + code);
            // documentParsed.setErrorMessage(response.statusMessage());
        }
        documentParsed.setDoc(doc);
        documentParsed.setCode(code);
        //  documentParsed = new DocumentParsed(doc, code);
        return documentParsed;
    }


    private void fillPageDtoAndSaveBD(PageDto pageDto, DocumentParsed documentParsed) {
        pageDto.setCode(documentParsed.getCode());
        //  Elements contentPage = documentParsed.getDoc().getAllElements();
        //  Elements contentPage = documentParsed.getDoc().select("body"); // get all content of the page from tag <body>
        Document contentPage = documentParsed.getDoc();

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
        return  (long) (beginningOfRange + (Math.random() * 4500));
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

    private void getLemmasFromPage(Document document, PageDto pageDto, SiteDto siteDto, PoolService poolService) {
        if (pageDto.getCode() == 200) {
            try {
                LemmaParser lemmaParser = new LemmaParser(poolService);
                Map<String, Integer> lemmasCountsMap = lemmaParser.getLemmasToAmountOnPageFromDocumentPage(document);
                lemmaParser.getLemmaDtoAndIndexDto(siteDto, pageDto, lemmasCountsMap);
            } catch (IOException | NullPointerException e) {
                Logger.getLogger(HtmlParser.class.getName()).info("catch IOEx lemma - " + e.getMessage());
                saveLastErrorInSiteEntity(e);
               // throw new RuntimeException(e.getMessage(), e.getCause());
                if (e instanceof IOException){
                    Logger.getLogger(HtmlParser.class.getName()).info("перед FailedConnectionException");
                    throw new FailedConnectionException(((IOException) e).getMessage());
                } else {
                    throw new IllegalArgumentException(((NullPointerException) e).getMessage(), ((NullPointerException) e).getCause());
                }
            }
        }
    }
}
