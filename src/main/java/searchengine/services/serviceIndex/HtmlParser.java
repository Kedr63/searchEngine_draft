package searchengine.services.serviceIndex;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.config.UserAgent;
import searchengine.dto.indexing.DocumentParsed;
import searchengine.dto.indexing.ThreadStopper;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.awt.geom.IllegalPathStateException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Logger;

@Getter
@Setter
public class HtmlParser extends RecursiveAction {

    private final String url;
    private final SiteEntity siteEntity;
    private final IndexServiceImp indexServiceImp;


    public HtmlParser(String url, SiteEntity siteEntity, IndexServiceImp indexServiceImp) {
        this.url = url;
        this.siteEntity = siteEntity;
        this.indexServiceImp = indexServiceImp;
    }


    @Override
    protected void compute() {
        List<HtmlParser> tasks = new ArrayList<>();
       // Set<String> paths = new HashSet<>();

       /* CopyOnWriteArrayList<Site> df;
        ConcurrentSkipListSet<String> links;*/

        try {
            DocumentParsed documentParsed = getParsedDocument(url);
            Document doc = documentParsed.getDoc();

            Elements searchLinks = doc.select("a[href^=/][href~=(/\\w*\\z|\\w/\\z|.html)]")
                    .not("[href*=#]");
            //📌 a[href^=/][href~=(/\w+\z|\w/\z|.html)] - в теге /а/ будет искать href начинающийся на "/", далее href с регулярным
            // выражением ("/" ноль или несколько букв, подчеркивание или цифр (\\w*) и это конец текста (\\z) | или в конце текста / (\w/\z) | или в конце
            // .html

            // добавим проверку по тэгу title: если такая страница уже участвовала в обработке, то не будем повторно использовать эту страницу
          /*  String titleString = doc.getElementsByTag("title").text();
            CollectionStorage.checkingAndAddToSetTitle(titleString);*/

            for (Element element : searchLinks) {

                // String linkHref = element.attr("href");
                String relHref = element.attr("href");
                String absHref = siteEntity.getUrl();
                String fullHref = absHref + relHref;
                Logger.getLogger(HtmlParser.class.getName()).info("fullHref: " + fullHref);


                // предварительно уберем повторяющиеся path
                // if BD have /path/ -> то перейдем к следующему элементу цикла
               /* synchronized (IndexServiceImp.lock) {
                    if (CollectionStorage.setPaths.contains(fullHref)) {
                        // throw new IllegalPathStateException(" такой путь есть - не используем и идем дальше");
                        Logger.getLogger(HtmlParser.class.getName()).info("такой путь есть - не используем и идем дальше -> use continue");
                        continue;
                    }
                }*/


                PageRepository pageRepository = indexServiceImp.getPageRepository();
                SiteRepository siteRepository = indexServiceImp.getSiteRepository();

                if (!isPresentPathInPageRepository(fullHref, pageRepository)) {
                    PageEntity pageEntity;
                    synchronized (IndexServiceImp.lock) {
                        pageEntity = createPageEntity(relHref, siteEntity);
                        pageRepository.save(pageEntity);

                        Logger.getLogger(HtmlParser.class.getName()).info("save ENTITY in repository: IT path - " + pageEntity.getPath());
                    }

                       // paths.add(pageEntity.getPath());


                    siteEntity.setStatusTime(LocalDateTime.now()); // update time indexing
                    siteRepository.save(siteEntity);

                    HtmlParser task = new HtmlParser(pageEntity.getPath(), siteEntity, indexServiceImp);
                    tasks.add(task);
                    task.fork();

                }




                    /*siteEntity.setStatusTime(LocalDateTime.now()); // update time indexing
                    siteRepository.save(siteEntity);

                    // MyLogger.logger.info("save Entity in pageRepository: it path - " + pageEntity.getPath());
                    Logger.getLogger(HtmlParser.class.getName()).info("save ENTITY in repository: IT path - " + pageEntity.getPath());

                    HtmlParser task = new HtmlParser(pageEntity.getPath(), siteEntity, indexServiceImp);
                    tasks.add(task);
                    task.fork();*/
                       /* try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }*/

                //}
                //   }
                // }
            }

         /*   if (!paths.isEmpty()) {
                for (HtmlParser task : tasks) {
                    MyLogger.logger.info("Зашли в цикл /for (HtmlParser task : tasks)/  - task.fork();");
                    task.fork();
                }
                for (HtmlParser task : tasks) {
                    MyLogger.logger.info("Зашли в цикл /for (HtmlParser task : tasks)/  - paths.addAll(task.join());");
                    paths.addAll(task.join());
                }
            } *//*else
                throw new IllegalStateException("Набор даже не набрался, здесь все пути-href использовали");*/

            if (!tasks.isEmpty()) {
              /*  for (HtmlParser task : tasks) {
                    task.fork();
                    Logger.getLogger(HtmlParser.class.getName()).info(" task.fork() ");
                }*/
                for (HtmlParser task : tasks) {
                    if (ThreadStopper.stopper) {
                        throw new InterruptedException("Stop thread");
                    }
                   // paths.addAll(task.join());
                    task.join();

                }
                //  addResultsFromTasks(paths, tasks);
            }
         //   Logger.getLogger(HtmlParser.class.getName()).info("paths собрал set and GO TO -> /return paths/ = " + paths.size());

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(HtmlParser.class.getName()).info("catch " + ex.getClass() + " ex");
            //  setLastErrorAndSave(ex, siteEntity, indexServiceImp);
            // throw new RuntimeException(ex.getMessage());
        } catch (IllegalPathStateException ex) {
            Logger.getLogger(HtmlParser.class.getName()).info(ex.getMessage());

            /* catch (InterruptedException ex) {
            setLastErrorAndSave(ex, siteEntity, indexServiceImp);
            Logger.getLogger(HtmlParser.class.getName()).info("catch InterruptedException ex");
            throw new RuntimeException(ex.getMessage());
        }*/
        }

      //  Logger.getLogger(HtmlParser.class.getName()).info("return paths - size = " + paths.size());
      //  return paths;
    }

    private boolean isPresentPathInPageRepository(String fullHref, PageRepository pageRepository) {
        synchronized (IndexServiceImp.lock) {
            return pageRepository.findByPath(fullHref).isPresent();
        }
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
        DocumentParsed documentParsed = null;
        Document doc;
        Connection.Response response;
        int code = 0;

        response = Jsoup.connect(url)
                .userAgent(generateUserAgent())
                .referrer("https://www.google.com")
                .ignoreHttpErrors(true)
              //  .ignoreContentType(true)
                .followRedirects(true)
                .timeout(10000)
                .execute();
        try {
            Thread.sleep(generateRandomRangeDelay()); // задержка между запросами
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (response.statusCode() == 200) {
            doc = response.parse();
            code = response.statusCode();
            documentParsed = new DocumentParsed(doc, code);
        } else {
            Logger.getLogger(HtmlParser.class.getName()).info("ошибка в: " + url + " code " + code);
            // throw new FailedSearching("ошибка в: " + url + " code " + code);
            // throw new IOException(" Ошибка индексации: сайт не доступен");

            //    documentParsed = new DocumentParsed(response.statusCode());
        }
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

    private PageEntity createPageEntity(String relHref, SiteEntity siteEntity) throws IOException {
        PageEntity pageEntity = new PageEntity();
        pageEntity.setPath(siteEntity.getUrl() + relHref);

        DocumentParsed documentParsed = getParsedDocument(pageEntity.getPath());
        pageEntity.setCode(documentParsed.getCode());
        pageEntity.setSiteEntity(siteEntity);
        /* Optional<Elements> optionalElements = Optional.of(documentParsed.getDoc().getAllElements());*/
        Elements contentPage = documentParsed.getDoc().getAllElements();  // get all content of the page

        // Elements contentPage = optionalElements.get();

        String contentViaString = "" + contentPage;
        pageEntity.setContent(contentViaString);

        return pageEntity;
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

}
