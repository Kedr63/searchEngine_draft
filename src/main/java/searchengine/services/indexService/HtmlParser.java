package searchengine.services.indexService;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import searchengine.config.UserAgent;
import searchengine.dto.indexing.DocumentParsed;
import searchengine.dto.indexing.ThreadStopper;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.services.PageService;

import java.awt.geom.IllegalPathStateException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@Getter
@Setter
public class HtmlParser extends RecursiveAction {

    private final String url;
    private SiteEntity siteEntity;
    private IndexServiceImp indexServiceImp;

    public HtmlParser(String url, SiteEntity siteEntity, IndexServiceImp indexServiceImp) {
        this.url = url;
        this.siteEntity = siteEntity;
        this.indexServiceImp = indexServiceImp;
    }

    @Override
    protected void compute() {
        List<HtmlParser> tasks = new ArrayList<>();

        String urlBase = siteEntity.getUrl();
        String linkLocate = url.replace(urlBase, "");
        if (linkLocate.isEmpty()) {
            linkLocate = "/";
        }

        try {
            Document doc;
            DocumentParsed documentParsed;
            PageEntity pageEntity;
            synchronized (IndexServiceImp.lock) {
                if (!isPresentPathsInPageRepository(linkLocate, indexServiceImp.getPageService())) {
                    pageEntity = new PageEntity();
                    pageEntity.setPath(linkLocate);
                    pageEntity.setContent("");
                  //  pageEntity.setSiteEntity(siteEntity);
                    PageService pageService = indexServiceImp.getPageService();
                    pageService.savePageEntity(pageEntity);
//
//                    PageEntity pageEntity = createPageEntity(linkLocate, documentParsed, siteEntity);
                    Logger.getLogger(HtmlParser.class.getName()).info("save path in repository:  - " + url);

                } else {
                    return;  // иначе если path есть в базе, то останавливаем здесь код
                }
            }

            synchronized (this) {
                documentParsed = getParsedDocument(url);
            }
            fillPageEntityAndSaveBD(pageEntity, documentParsed); // заполним pageEntity остальными данными
            siteEntity.setStatusTime(LocalDateTime.now());
            indexServiceImp.getSiteService().saveSiteEntity(siteEntity);

            doc = documentParsed.getDoc();




           /* // добавим проверку по тэгу title: если такая страница уже участвовала в обработке, то не будем повторно использовать эту страницу
            String titleString = doc.getElementsByTag("title").text();
            CollectionStorage.checkingAndAddToSetTitle(titleString);*/


//            Elements searchLinks = doc.select("a[href^=/][href~=(/\\w*\\z|\\w/\\z|.html)]")
//                    .not("[href*=#]");
            List<String> searchLinks = doc.select("a[href^=/][href~=(/\\w*\\z|\\w/\\z|.html)]")
                    .not("[href*=#]").stream().map(element -> element.attr("href"))
                    .distinct().collect(Collectors.toList());
            //📌 a[href^=/][href~=(/\w+\z|\w/\z|.html)] - в теге /а/ будет искать href начинающийся на "/", далее href с регулярным
            // выражением ("/" ноль или несколько букв, подчеркивание или цифр (\\w*) и это конец текста (\\z) | или в конце текста / (\w/\z)
            // | или в конце .html

//           Elements searchLinks = doc.select("a[href^=/][href~=(/\\w*\\z|\\w/\\z|.html)]")
//                    .not("[href*=#]").stream().distinct().collect(Collectors.toCollection(Elements::new));


            Logger.getLogger(HtmlParser.class.getName()).info(IndexServiceImp.tmp = "return end");

            for (String link : searchLinks) {
                if (ThreadStopper.stopper) {
                    throw new InterruptedException("Stop thread");
                }

//                synchronized (IndexServiceImp.lock) {
//                    if (!CollectionStorage.checkingAndAddToSetLink(link)) {
//                        continue;   // если false, т.е. есть уже в коллекции такой путь (существует), то идем к следующему элементу loop
//                    }
//                }
                synchronized (IndexServiceImp.lock) {
                    if (isPresentPathsInPageRepository(link, indexServiceImp.getPageService())) {
                        continue;
                    }
                }

                String fullHref = siteEntity.getUrl() + link;
                Logger.getLogger(HtmlParser.class.getName()).info("fullHref: " + fullHref);

//                HtmlParser task = new HtmlParser(fullHref);
//                task.setIndexServiceImp(indexServiceImp);
//                task.setSiteEntity(siteEntity);
                HtmlParser task = new HtmlParser(fullHref, siteEntity, indexServiceImp);

                task.fork();
                System.out.println("countOfHtmlParser = " + IndexServiceImp.countOfHtmlParser++);

                tasks.add(task);
            }


            if (!tasks.isEmpty()) {

//                forkJoinForBigTasks(tasks);
//                Logger.getLogger(HtmlParser.class.getName()).info("forkJoinForBigTasks(tasks)");

                for (HtmlParser task : tasks) {
                    task.join();
                    Logger.getLogger(HtmlParser.class.getName()).info("task.join()");
                }

                tasks.clear();
                Logger.getLogger(HtmlParser.class.getName()).info("tasks.clear()");

//
            } else {
                Logger.getLogger(HtmlParser.class.getName()).info("tasks.isEmpty()  - список задач пуст 📌");
            }

        } catch (IOException ex) {
            Logger.getLogger(HtmlParser.class.getName()).info("catch " + ex.getClass() + " ex");
            throw new RuntimeException(ex.getCause());
            //  setLastErrorAndSave(ex, siteEntity, indexServiceImp);

        } catch (IllegalPathStateException | NullPointerException ex) {
            Logger.getLogger(HtmlParser.class.getName()).info(ex.getMessage() + " check ?");
            throw new NullPointerException(ex.getLocalizedMessage());

        } catch (InterruptedException ex) {
            throw new RuntimeException(ex.getCause()); // пробросим RuntimeException, чтоб остановить forkJoinPool

        }
    }


    private void forkJoinForBigTasks(List<HtmlParser> tasks) {
        List<HtmlParser> beforeIterateTasks = new ArrayList<>(tasks);
        List<HtmlParser> afterIterateTasks = new ArrayList<>(beforeIterateTasks);
        List<HtmlParser> tasksForJoin = new ArrayList<>();
        int counterTasks = IndexServiceImp.coreAmount;
        for (HtmlParser task : beforeIterateTasks) {
            tasksForJoin.add(task);
            task.fork();
            Logger.getLogger(HtmlParser.class.getName()).info("task.fork()");
            afterIterateTasks.remove(task);
            counterTasks--;
            if (counterTasks == 0) {
                break;
            }
        }
        for (HtmlParser htmlParser : tasksForJoin) {
            htmlParser.join();
            Logger.getLogger(HtmlParser.class.getName()).info("task.join() ");

        }
        if (afterIterateTasks.size() > IndexServiceImp.coreAmount && !afterIterateTasks.isEmpty()) {
            forkJoinForBigTasks(afterIterateTasks);
        }
       /* if (afterIterateTasks.isEmpty()){
            System.out.println("afterIterateTasks.isEmpty() !!!!!!!!!!!!!!!!!");
        }*/
    }

    private boolean isPresentPathsInPageRepository(String fullHref, PageService pageService) {
        // synchronized (IndexServiceImp.lock) {
        return pageService.isPresentPageEntityByPath(fullHref);
        //  }

    }

   /* private PageEntity startCreatePageEntity(String relHref, SiteEntity siteEntity) {
        PageEntity pageEntity = new PageEntity();
        pageEntity.setPath(siteEntity.getUrl() + relHref);
        return pageEntity;
    }*/

   /* private void addResultsFromTasks(Set<String> paths, List<HtmlParser> tasks) throws InterruptedException {
        for (HtmlParser task : tasks) {
            if (ThreadStopper.stopper) {
                throw new InterruptedException("Stop thread");
            }
            paths.addAll(task.join());
        }
    }*/

/*    private void setLastErrorAndSave(Exception ex, SiteEntity siteEntity, IndexServiceImp indexServiceImp) {
        siteEntity.setLastError(ex.getMessage());
        siteEntity.setStatus(StatusIndex.FAILED);
        indexServiceImp.getSiteRepository().save(siteEntity);
        Logger.getLogger(HtmlParser.class.getName()).info("Отработал метод setLastErrorAndSave для " + siteEntity.getName());
    }*/

    private DocumentParsed getParsedDocument(String url) throws IOException {
        DocumentParsed documentParsed;
        Document doc;
        Connection.Response response;
        int code;

        response = Jsoup.connect(url)
                .userAgent(generateUserAgent())
                .referrer("https://www.google.com")
                .ignoreHttpErrors(true)
                //  .ignoreContentType(true)
                .followRedirects(true)
                .timeout(60000)
                .execute();
        try {
            Thread.sleep(generateRandomRangeDelay()); // задержка между запросами
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (response.statusCode() == 200) {
            doc = response.parse();
            code = response.statusCode();
            //  documentParsed = new DocumentParsed(doc, code);
        } else {
            doc = new Document(url);
            code = response.statusCode();
            Logger.getLogger(HtmlParser.class.getName()).info("ошибка в: " + url + " code " + code);
            // documentParsed = new DocumentParsed(doc.body() , codeError);
            // throw new FailedSearching("ошибка в: " + url + " code " + code);
            // throw new IOException(" Ошибка индексации: сайт не доступен");

            //    documentParsed = new DocumentParsed(response.statusCode());
        }
        documentParsed = new DocumentParsed(doc, code);
        return documentParsed;
    }

    private static boolean isMatchRequiredConditions(String searchHref, String linkCore) {
        return
                searchHref.replaceAll("(ht{2}ps?://[a-z]+\\.\\w+)?(/)?([/{1}][\\w/\\-]+\\.?h?t?m?l?)", "$1")
                        .compareTo(linkCore) == 0 ||
                        searchHref.replaceAll("(ht{2}ps?://[a-z]+\\.\\w+)?(/)?([/{1}][\\w/\\-]+\\.?h?t?m?l?)",
                                "$1").isEmpty() && searchHref.replaceAll(
                                        "(ht{2}ps?:)?(//[a-z]+\\.\\w+)?(/)?([/{1}][\\w/\\-]+\\.?h?t?m?l?)", "$4")
                                .startsWith("/");
    }

    private PageEntity createPageEntity(String link, DocumentParsed documentParsed, SiteEntity siteEntity) throws IOException {
        PageEntity pageEntity = new PageEntity();
        pageEntity.setPath(link);

        //  DocumentParsed documentParsed = getParsedDocument(pageEntity.getPath());
        pageEntity.setCode(documentParsed.getCode());
        pageEntity.setSiteEntity(siteEntity);
        /* Optional<Elements> optionalElements = Optional.of(documentParsed.getDoc().getAllElements());*/
        Elements contentPage = documentParsed.getDoc().getAllElements();  // get all content of the page

        // Elements contentPage = optionalElements.get();

        String contentViaString = "" + contentPage;
        pageEntity.setContent(contentViaString);

        return pageEntity;
    }

    private void fillPageEntityAndSaveBD(PageEntity pageEntity, DocumentParsed documentParsed) {
        pageEntity.setCode(documentParsed.getCode());
        pageEntity.setSiteEntity(siteEntity);
        Elements contentPage = documentParsed.getDoc().getAllElements();  // get all content of the page
        String contentViaString = "" + contentPage;
        pageEntity.setContent(contentViaString);
        PageService pageService = indexServiceImp.getPageService();
        synchronized (IndexServiceImp.lock) {
            pageService.savePageEntity(pageEntity);
        }
//        siteEntity.setStatusTime(LocalDateTime.now());
//        indexServiceImp.getSiteService().saveSiteEntity(siteEntity);

        // updateSiteEntity(siteEntity, documentParsed);

    }

    private String generateUserAgent() {

        List<UserAgent> userAgents = indexServiceImp.getUserAgentList().getUserAgents();

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
        long rangeRandom = (long) (beginningOfRange + (Math.random() * 4500));
        return rangeRandom;
    }

    private void updateSiteEntity(SiteEntity siteEntity, DocumentParsed documentParsed) {
        if (documentParsed.getCode() != 200) {
            String messageError = String.valueOf(HttpStatus.resolve(documentParsed.getCode()));
            siteEntity.setLastError(messageError);
        }
        siteEntity.setStatusTime(LocalDateTime.now());
        indexServiceImp.getSiteService().saveSiteEntity(siteEntity);
    }
}
