package searchengine.services.indexService;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import searchengine.config.UserAgent;
import searchengine.dto.indexing.DocumentParsed;
import searchengine.dto.indexing.ThreadStopper;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.services.PageService;

import java.awt.geom.IllegalPathStateException;
import java.io.IOException;
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


    //   private UserAgentList userAgentList;
    // private IndexService indexService;
    // private PageService pageService;
    // private SiteService siteService;

    public HtmlParser(String url, SiteEntity siteEntity, IndexServiceImp indexServiceImp) {
        this.url = url;
        this.siteEntity = siteEntity;
        this.indexServiceImp = indexServiceImp;
    }

    public HtmlParser(String url) {
        this.url = url;
    }

    @Override
    protected void compute() {
        List<HtmlParser> tasks = new ArrayList<>();
        //   StringBuilder builder = new StringBuilder();

        String urlBase = siteEntity.getUrl();
        String linkLocate = url.replace(urlBase, "");
        if (linkLocate.isEmpty()) {
            linkLocate = "/";
        }

        try {
            Document doc;


            synchronized (IndexServiceImp.lock) {
                if (!isPresentPathsInPageRepository(linkLocate, indexServiceImp.getPageService())) {
                   DocumentParsed documentParsed = getParsedDocument(url);
                    doc = documentParsed.getDoc();

                    PageService pageService = indexServiceImp.getPageService();
                    PageEntity pageEntity = createPageEntity(documentParsed, siteEntity);
                    pageService.savePageEntity(pageEntity);
                    Logger.getLogger(HtmlParser.class.getName()).info("save PageEntity in repository: it path - " + url);

                }  else {
                    Logger.getLogger(HtmlParser.class.getName()).info(IndexServiceImp.tmp ="return start");
                    return;
                    //  this.cancel(true);
                 //   Logger.getLogger(HtmlParser.class.getName()).info("this.cancel " + this.isCancelled());
                }
            }

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


            Logger.getLogger(HtmlParser.class.getName()).info(IndexServiceImp.tmp ="return end");

            for (String link : searchLinks) {
                if (ThreadStopper.stopper) {
                    throw new InterruptedException("Stop thread");
                }

//                synchronized (IndexServiceImp.lock) {
//                    if (!CollectionStorage.checkingAndAddToSetLink(link)) {
//                        continue;   // если false, т.е. есть уже в коллекции такой путь (существует), то идем к следующему элементу loop
//                    }
//                }
//                synchronized (IndexServiceImp.lock) {
//                    if (isPresentPathsInPageRepository(link, indexServiceImp.getPageService())) {
//                        continue;
//                    }
//                }

                String fullHref = siteEntity.getUrl() + link;
                Logger.getLogger(HtmlParser.class.getName()).info("fullHref: " + fullHref);

                HtmlParser task = new HtmlParser(fullHref);
                task.setIndexServiceImp(indexServiceImp);
                task.setSiteEntity(siteEntity);
                //  HtmlParser task = new HtmlParser(fullHref, siteEntity, indexServiceImp);

                System.out.println("countOfHtmlParser = " + IndexServiceImp.countOfHtmlParser++);

                tasks.add(task);

                // builder.append("'" + fullHref + "'")


                // предварительно уберем повторяющиеся path
                // if BD have /path/ -> то перейдем к следующему элементу цикла
               /* synchronized (IndexServiceImp.lock2) {
                    if (CollectionStorage.setPaths.contains(fullHref)) {
                        // throw new IllegalPathStateException(" такой путь есть - не используем и идем дальше");
                        Logger.getLogger(HtmlParser.class.getName()).info("такой путь есть - не используем и идем дальше -> use continue");
                        continue;
                    }
                }*/


//                if (!isPresentPathsInPageRepository(fullHref, indexServiceImp.getPageService())) {
//                    PageEntity pageEntity;
//                    synchronized (IndexServiceImp.lock) {
//                        pageEntity = createPageEntity(link, siteEntity);
//                        indexServiceImp.getPageService().savePageEntity(pageEntity);
//
//                        Logger.getLogger(HtmlParser.class.getName()).info("save PageEntity in repository: it path - " + pageEntity.getPath());
//                    }
//
//                HtmlParser task = new HtmlParser(fullHref, siteEntity, indexServiceImp);
//
//                    System.out.println("countOfHtmlParser = " + IndexServiceImp.countOfHtmlParser++);
//
//                    //  task.fork();
//                    tasks.add(task);
//
//                }

            }


            if (!tasks.isEmpty()) {

                //  List<HtmlParser> tasksForParser = new ArrayList<>(tasks); // создадим копию tasks для работы с ней (передадим в конструктор tasks)
                //    for (int i = 0; i < tasks.size(); i++) {
                //   if (tasks.size() < IndexServiceImp.coreAmount) {

                forkJoinForBigTasks(tasks);
                Logger.getLogger(HtmlParser.class.getName()).info("forkJoinForBigTasks(tasks)");

                tasks.clear();
                Logger.getLogger(HtmlParser.class.getName()).info("tasks.clear()");

//                    for (HtmlParser task : tasks) {
//                        task.fork();
//                        Logger.getLogger(HtmlParser.class.getName()).info(" task.fork() ");
//                    }


                //  }
//                if (tasks.size() >= IndexServiceImp.coreAmount) {
//                    forkJoinForBigTasks(tasks);
//                    Logger.getLogger(HtmlParser.class.getName()).info("tasks.size() >=    task.fork() + join()");
//                }
                //  }


//            for (HtmlParser task : tasks) {
//                task.join();
//                Logger.getLogger(HtmlParser.class.getName()).info("task.join()");
//            }

                /*int sizeTasks = tasks.size();
                Logger.getLogger(HtmlParser.class.getName()).info("tasks.size() = " + tasks.size());
                int middleHalf = sizeTasks/2;
                Logger.getLogger(HtmlParser.class.getName()).info("middleHalf = " + sizeTasks/2);

                for (int i = 0; i < middleHalf; i++) {
                    tasks.get(i).fork();
                    //task.fork();
                    Logger.getLogger(HtmlParser.class.getName()).info("task.fork1");
                 //   System.out.println("countOfHtmlParser = " + IndexServiceImp.countOfHtmlParser--);
                }
                for (int i = 0; i < middleHalf; i++){
                    tasks.get(i).join();
                 //   task.join();
                    Logger.getLogger(HtmlParser.class.getName()).info("task.join()1");
                }


                for (int i = middleHalf; i < sizeTasks; i++) {
                    tasks.get(i).fork();
                    //task.fork();
                    Logger.getLogger(HtmlParser.class.getName()).info("task.fork2");
                    //   System.out.println("countOfHtmlParser = " + IndexServiceImp.countOfHtmlParser--);
                }
                for (int i = middleHalf; i < sizeTasks; i++){
                    tasks.get(i).join();
                    //   task.join();
                    Logger.getLogger(HtmlParser.class.getName()).info("task.join()2");
                }*/

            } else {
                Logger.getLogger(HtmlParser.class.getName()).info("tasks.isEmpty()  - список задач пуст 📌");
            }
            //  int amount = CollectionStorage.setPaths.size();
            //  Logger.getLogger(HtmlParser.class.getName()).info("setPaths.size() = " + amount);







            /*for (HtmlParser task : tasks){
                task.join();
                Logger.getLogger(HtmlParser.class.getName()).info("task.join()");
            }*/
           /* for (HtmlParser task : tasks){
                task.cancel(true);
                Logger.getLogger(HtmlParser.class.getName()).info("task.cancel(true)");
                Logger.getLogger(HtmlParser.class.getName()).info("task.isCancelled()  - " + task.isCancelled());
            }*/

            // Thread.currentThread().interrupt();
            // Logger.getLogger(HtmlParser.class.getName()).info("Thread.currentThread().interrupt()");

           /* for (HtmlParser task : tasks){
                task.quietlyComplete();
                Logger.getLogger(HtmlParser.class.getName()).info(" task.quietlyComplete()");
            }*/

          /*  if (!paths.isEmpty()) {
                for (HtmlParser task : tasks) {
                    MyLogger.logger.info("Зашли в цикл /for (HtmlParser task : tasks)/  - task.fork();");
                    task.fork();
                }
                for (HtmlParser task : tasks) {
                    MyLogger.logger.info("Зашли в цикл /for (HtmlParser task : tasks)/  - paths.addAll(task.join());");
                    paths.addAll(task.join());
                }
            } else
                throw new IllegalStateException("Набор даже не набрался, здесь все пути-href использовали");*/


            /* if (!tasks.isEmpty()) {
                 for (HtmlParser task : tasks) {
                     task.fork();
                     Logger.getLogger(HtmlParser.class.getName()).info(" task.fork() ");
                     // task.join();
                     //  Logger.getLogger(HtmlParser.class.getName()).info("after  task.join() ");

                 }
             }*/
             /*  for (HtmlParser task : tasks) {
                    if (ThreadStopper.stopper) {
                        throw new InterruptedException("Stop thread");
                    }
                   // task.fork();
                  //  Logger.getLogger(HtmlParser.class.getName()).info(" task.fork() ");
                    // paths.addAll(task.join());
                    task.join();
                    Logger.getLogger(HtmlParser.class.getName()).info("after  task.join() ");


                }
                //  addResultsFromTasks(paths, tasks);
            }*/


            // tasks.clear();
            //   Logger.getLogger(HtmlParser.class.getName()).info("paths собрал set and GO TO -> /return paths/ = " + paths.size());

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


        //  Logger.getLogger(HtmlParser.class.getName()).info("return paths - size = " + paths.size());
        //  return paths;
    }




   /* private void forkJoin(List<HtmlParser> tasks) {
        for (HtmlParser task : tasks) {
            task.fork();
            Logger.getLogger(HtmlParser.class.getName()).info("task.fork()");

        }
        int indexTasks = 0;
        for (HtmlParser task : tasks) {
            task.join();
            Logger.getLogger(HtmlParser.class.getName()).info("task.join()");
            tasks.remove(indexTasks);
            indexTasks++;
        }
    }*/

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
                .timeout(30000)
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
            int codeError = response.statusCode();
            Logger.getLogger(HtmlParser.class.getName()).info("ошибка в: " + url + " code " + codeError);
            // documentParsed = new DocumentParsed(doc.body() , codeError);
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

    private PageEntity createPageEntity(DocumentParsed documentParsed, SiteEntity siteEntity) throws IOException {
        PageEntity pageEntity = new PageEntity();
        String urlBase = siteEntity.getUrl();
        String baseUriDoc = documentParsed.getDoc().baseUri();
        String linkLocate = baseUriDoc.replace(urlBase, "");
        if (linkLocate.isEmpty()) {
            linkLocate = "/";
        }
        pageEntity.setPath(linkLocate);

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
