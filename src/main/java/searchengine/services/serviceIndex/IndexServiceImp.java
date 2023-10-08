package searchengine.services.serviceIndex;

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
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

@Service
@Getter
@Setter
public class IndexServiceImp implements IndexService {

    private final SiteRepository siteRepository;

    private final PageRepository pageRepository;

    private final SitesList sitesList;

    private final UserAgentList userAgentList;

    protected static final Object lock = new Object();

    public IndexServiceImp(SiteRepository siteRepository, PageRepository pageRepository, SitesList sitesList, UserAgentList userAgentList) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.sitesList = sitesList;
        this.userAgentList = userAgentList;
    }


    @Override
    public IndexResponse startIndexing() {

        siteRepository.deleteAll();
        if (siteRepository.findAll().stream().anyMatch(o -> o.getStatus().equals(StatusIndex.INDEXING))) {
            return new IndexResponseError(false, "Индексация уже запущена");

        }
        IndexServiceImp indexServiceImp = new IndexServiceImp(siteRepository, pageRepository, sitesList, userAgentList);


        int sizeSitesList = indexServiceImp.getSitesList().getSites().size();

       /* ExecutorService executorService = Executors.newFixedThreadPool(sizeSitesList);
        List<Future<Set<String>>> futureResult = new ArrayList<>();*/

      /*  ThreadStopper threadStopper = new ThreadStopper();
        threadStopper.setStopper(false);*/
ForkJoinPool forkJoinPool = new ForkJoinPool();

        for (int i = 0; i < sizeSitesList; i++) {
            Site site = indexServiceImp.getSitesList().getSites().get(i);
            //   String url = site.getUrl();

            SiteEntity siteEntity = createSiteEntity(site);
            indexServiceImp.getSiteRepository().save(siteEntity);

            HtmlParser parser = new HtmlParser(siteEntity.getUrl(), siteEntity, indexServiceImp);
            forkJoinPool.invoke(parser);
            Logger.getLogger(IndexServiceImp.class.getName()).info("forkJoinPool.execute(parser);");

            do {
                Logger.getLogger(IndexServiceImp.class.getName()).info("Work parser " + parser.getUrl());
            }
            while (!parser.isDone());
            forkJoinPool.shutdown();
            parser.join();
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

        }

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
        Logger.getLogger(IndexServiceImp.class.getName()).info("executorService.shutdown();");

       /* IndexResponse indexResponse = new IndexResponse(true);
          IndexResponseError indexResponseError = new IndexResponseError(false, "не удалось");*/


        List<SiteEntity> siteEntityList = siteRepository.findAll();
        if (siteEntityList.stream().allMatch(siteEntity -> siteEntity.getStatus().equals(StatusIndex.INDEXED))) {
            return new IndexResponse(true);
        }

        return new IndexResponseError(false, "что-то пошло тне так");
    }

    @Override
    public IndexResponseError stopIndexing() {
        String error = "Индексация остановлена пользователем";

     /* for(Thread thread : Thread.getAllStackTraces().keySet()){
          thread.interrupt();
      }*/
        ThreadStopper.stopper = true; // даем согласие, чтоб в классе HtmlParser выбросилось исключение и forkJoinPool остановился


        Logger.getLogger(IndexServiceImp.class.getName()).info("ThreadStopper.stopper = true");

        List<searchengine.model.SiteEntity> siteEntityList = siteRepository.findAll();
        for (searchengine.model.SiteEntity siteEntity : siteEntityList) {
            if (siteEntity.getStatus().equals(StatusIndex.INDEXING)) {
                siteEntity.setStatus(StatusIndex.FAILED);
                siteEntity.setLastError(error);
                siteEntity.setStatusTime(LocalDateTime.now());
                siteRepository.save(siteEntity);

            }
            Logger.getLogger(IndexServiceImp.class.getName()).info("in loop stopIndex");
        }
        Logger.getLogger(IndexServiceImp.class.getName()).info("before - return new IndexResponseError(false, error);");
        return new IndexResponseError(false, error);
    }


    private searchengine.model.SiteEntity createSiteEntity(Site site) {
        searchengine.model.SiteEntity siteEntity = new searchengine.model.SiteEntity();
        siteEntity.setStatus(StatusIndex.INDEXING);
        siteEntity.setStatusTime(LocalDateTime.now());
        siteEntity.setUrl(site.getUrl());
        siteEntity.setName(site.getName());

        return siteEntity;
    }

}
