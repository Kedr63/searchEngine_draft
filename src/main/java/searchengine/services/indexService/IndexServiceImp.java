package searchengine.services.indexService;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.config.UserAgentList;
import searchengine.dto.indexing.IndexResponse;
import searchengine.dto.indexing.IndexResponseError;
import searchengine.dto.indexing.ThreadStopper;
import searchengine.model.SiteEntity;
import searchengine.model.StatusIndex;
import searchengine.services.PageService;
import searchengine.services.SiteService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service //при этой аннотации получим бины (объекты) прописанные в полях (но только если их добавим в конструктор)
@Getter
@Setter
public class IndexServiceImp implements IndexService {

    private final SiteService siteService;

    private final PageService pageService;

    private final SitesList sitesList;

    private final UserAgentList userAgentList;

  //  private final Limiter limiter;

    protected static final Object lock = new Object();
    protected static final Object lock2 = new Object();

    protected static long countOfHtmlParser = 1;
  //  protected static long counter = 0;
    protected static int coreAmount = Runtime.getRuntime().availableProcessors();

    protected static String tmp = "Return";


    public IndexServiceImp(SiteService siteService, PageService pageService, SitesList sitesList, UserAgentList userAgentList) {
        this.siteService = siteService;
        this.pageService = pageService;
        this.sitesList = sitesList;
        this.userAgentList = userAgentList;
    }


    @Override
    public IndexResponse startIndexing() {

        long start = System.currentTimeMillis();


        if (siteService.getAllSiteEntities().stream().anyMatch(o -> o.getStatus().equals(StatusIndex.INDEXING))) {
            return new IndexResponseError(false, "Индексация уже запущена");

        }

        siteService.deleteAll();

        int sizeSitesList = this.getSitesList().getSites().size();
        //  int sizeSitesList = sitesList.getSites().size();
        ExecutorService executorService = Executors.newFixedThreadPool(sizeSitesList);

        for (int i = 0; i < sizeSitesList; i++) {
            ForkJoinPool forkJoinPool = new ForkJoinPool();

            Site site = sitesList.getSites().get(i);

            SiteEntity siteEntity = createSiteEntity(site);
            siteService.saveSiteEntity(siteEntity);

            try {
                HtmlParser parser = new HtmlParser(siteEntity.getUrl(), siteEntity, this);
                forkJoinPool.invoke(parser); // пока метод не отработает со всеми fork код дальше не пойдет

                // parser.join();
                Logger.getLogger(IndexServiceImp.class.getName()).info("invoke(parser)");
                Logger.getLogger(IndexServiceImp.class.getName()).info("forkJoinPool.invoke(parser);");

                if (parser.isDone()) {
                    forkJoinPool.shutdown();
                    siteEntity.setStatus(StatusIndex.INDEXED);
                    //  indexServiceImp.getSiteService().saveSiteEntity(siteEntity);
                    Logger.getLogger(IndexServiceImp.class.getName()).info("siteEntity.setStatus(StatusIndex.INDEXED)");
                    this.getSiteService().saveSiteEntity(siteEntity);
                } // else return new IndexResponseError(false, "error indexing");


            } catch (Exception e) {
                forkJoinPool.shutdown();
                siteEntity.setStatus(StatusIndex.FAILED);
                siteEntity.setLastError(getShortMessageOfException(e));
                Logger.getLogger(IndexServiceImp.class.getName()).info("поймали  - " + e.getMessage());

                // indexServiceImp.getSiteService().saveSiteEntity(siteEntity);
                siteService.saveSiteEntity(siteEntity);

                return new IndexResponseError(false, siteEntity.getLastError());
            }

        }

        //  Logger.getLogger(IndexServiceImp.class.getName()).info("stringSet   size = " + stringSet.size());




           /* try {
                SiteForExecutorService siteForExecutorService = new SiteForExecutorService(siteEntity, indexServiceImp);
                Future<Set<String>> futureSetString = executorService.submit(siteForExecutorService);
                Logger.getLogger(IndexServiceImp.class.getName()).info("Future<Set<String>> futureSetString = executorService.submit(siteForExecutorService);");

                try {
                    Set<String> stringSet = futureSetString.get();
                    Logger.getLogger(IndexServiceImp.class.getName()).info("futureSetString.get();");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }

                futureResult.add(futureSetString);
                Logger.getLogger(IndexServiceImp.class.getName()).info("После futureResult.add(futureSetString); Size futureResult = " + futureResult.size());
            } catch (RuntimeException ex) {
                siteEntity.setStatusTime(LocalDateTime.now());
                siteEntity.setStatus(StatusIndex.FAILED);
                siteEntity.setLastError(ex.getMessage() + " + поймали в классе IndexServiceImp");
               // ThreadStopper.stopper = true;
                Logger.getLogger(IndexServiceImp.class.getName()).info("Ловим RuntimeException в классе IndexServiceImp в методе startIndexing и тормозим поток который его поймал");
            }*/



       /*     try {
               if (futureSetString.get() != null){
                   Logger.getLogger(IndexServiceImp.class.getName()).info("После if (futureSetString.get() != null)");

                   siteEntity.setStatus(StatusIndex.INDEXED);
                   siteRepository.save(siteEntity);
               };
            } catch (InterruptedException e) {
                siteEntity.setStatus(StatusIndex.FAILED);
                siteEntity.setLastError(e.getMessage());
                siteRepository.save(siteEntity);
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }*/

          /*  if (indexServiceImp.siteRepository.findAll().stream().allMatch(o->o.getUrl().equals(url))){
                indexServiceImp.siteRepository.delete(siteEntity); // если в БД есть этот сайт, то удалить его и начать новую индексацию
            }*/



           /* try {
                ForkJoinPool forkJoinPool = new ForkJoinPool();
                HtmlParser htmlParser = new HtmlParser(site.getUrl(), siteEntity, indexServiceImp);
                forkJoinPool.invoke(htmlParser);
            } catch (RuntimeException ex){
                Logger.getLogger(IndexServiceImp.class.getName()).info("Поймали ex из класса HtmlParser");
                return new IndexResponseError(false, ex.getMessage());
            }*/

          /*  if (ThreadStopper.stopper || TestIOEcxeption.throwTest){  // если значение стоппера - true, то остановливаем потоки и прерываем цикл
                break;
            }*/


        // if (ThreadStopper.stopper == false){
              /*  siteEntity.setStatus(StatusIndex.INDEXED);
                indexServiceImp.getSiteRepository().save(siteEntity);
                Logger.getLogger(IndexServiceImp.class.getName()).info("в методе startIndexing после строки siteEntity.setStatus(StatusIndex.INDEXED);\n" +
                        "            indexServiceImp.getSiteRepository().save(siteEntity);");*/
        // }



       /* for (Future<SiteEntity> result : futureResult) {
            try {
                if (result.isDone()) {
                    SiteEntity siteEntity = result.get();
                 //   SiteEntity siteEntity = siteEntityToSetString.getSiteEntity();

                   // siteEntity.setStatus(StatusIndex.INDEXED);
                  //  indexServiceImp.getSiteRepository().save(siteEntity);
                } else {
                    SiteEntity siteEntity = result.get();
                   // SiteEntity siteEntity = siteEntityToSetString.getSiteEntity();

                    siteEntity.setStatus(StatusIndex.FAILED);
                    indexServiceImp.siteRepository.save(siteEntity);
                }
            } catch (InterruptedException e) {

                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
         *//*   siteEntity.setStatus(StatusIndex.INDEXED);
            indexServiceImp.getSiteRepository().save(siteEntity);
            Logger.getLogger(IndexServiceImp.class.getName()).info("в методе startIndexing после строки siteEntity.setStatus(StatusIndex.INDEXED);\n" +
                    "            indexServiceImp.getSiteRepository().save(siteEntity);");*//*

        }*/

        // executorService.shutdown();
        //  Logger.getLogger(IndexServiceImp.class.getName()).info("executorService.shutdown();");

       /* IndexResponse indexResponse = new IndexResponse(true);
          IndexResponseError indexResponseError = new IndexResponseError(false, "не удалось");*/


        List<SiteEntity> siteEntityList = siteService.getAllSiteEntities();
        if (siteEntityList.stream().allMatch(siteEntity -> siteEntity.getStatus().equals(StatusIndex.INDEXED))) {
            long end = System.currentTimeMillis();
            double totalTime = (double) (end - start) / 60_000;
            Logger.getLogger(IndexServiceImp.class.getName()).info("totalTime = " + totalTime);
            return new IndexResponse(true);
        }

        return new IndexResponseError(false, "что-то пошло не так");
    }

    @Override
    public IndexResponseError stopIndexing() {
        String error = "Индексация остановлена пользователем";

     /* for(Thread thread : Thread.getAllStackTraces().keySet()){
          thread.interrupt();
      }*/
        ThreadStopper.stopper = true; // даем согласие, чтоб в классе HtmlParser выбросилось исключение и forkJoinPool остановился


        Logger.getLogger(IndexServiceImp.class.getName()).info("ThreadStopper.stopper = true");

        List<SiteEntity> siteEntityList = siteService.getAllSiteEntities();
        for (SiteEntity siteEntity : siteEntityList) {
            if (siteEntity.getStatus().equals(StatusIndex.INDEXING)) {
                siteEntity.setStatus(StatusIndex.FAILED);
                siteEntity.setLastError(error);
                siteEntity.setStatusTime(LocalDateTime.now());
                siteService.saveSiteEntity(siteEntity);

            }
            Logger.getLogger(IndexServiceImp.class.getName()).info("in loop stopIndex");
        }
        Logger.getLogger(IndexServiceImp.class.getName()).info("before - return new IndexResponseError(false, error);");
        return new IndexResponseError(false, error);
    }


    private SiteEntity createSiteEntity(Site site) {
        SiteEntity siteEntity = new SiteEntity();
        siteEntity.setStatus(StatusIndex.INDEXING);
        siteEntity.setStatusTime(LocalDateTime.now());
        siteEntity.setUrl(site.getUrl());
        siteEntity.setName(site.getName());

        return siteEntity;
    }


    private String getShortMessageOfException(Exception e) {
        StringBuilder builder = new StringBuilder();
        String[] arrayMessage = e.getMessage().split(":");
        List<String> stringList = Arrays.stream(arrayMessage).collect(Collectors.toList());
        builder.append(stringList.get(stringList.size() - 2)).append(" - ");
        builder.append(stringList.get(stringList.size() - 1));
        return builder.toString();
    }

}


