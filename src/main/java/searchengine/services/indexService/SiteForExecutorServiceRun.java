package searchengine.services.indexService;

import lombok.Getter;
import lombok.Setter;
import searchengine.model.SiteEntity;
import searchengine.model.StatusIndex;

import java.time.LocalDateTime;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;

@Getter
@Setter
public class SiteForExecutorServiceRun implements Runnable {
    // private Site url;
    private SiteEntity siteEntity;

    private IndexServiceImp indexServiceImp;

    public SiteForExecutorServiceRun(SiteEntity siteEntity, IndexServiceImp indexServiceImp) {
        //  this.url = url;
        this.siteEntity = siteEntity;
        this.indexServiceImp = indexServiceImp;
    }

//    @Override
//    public Set<String> call() throws Exception {
//        Set<String> stringSetResult = new HashSet<>();
//        //  SiteEntityToSetString siteEntityToSetString = new SiteEntityToSetString();
//        ForkJoinPool forkJoinPool = new ForkJoinPool();
//        try {
//            HtmlParser htmlParser = new HtmlParser(siteEntity.getUrl(), siteEntity, indexServiceImp);
//            forkJoinPool.invoke(htmlParser);
//          //  forkJoinPool.execute(htmlParser);
//
//
//          //  Logger.getLogger(SiteForExecutorService.class.getName()).info("Пришел результат stringSetResult size() = " + stringSetResult.size());
//            Logger.getLogger(SiteForExecutorServiceRun.class.getName()).info("Отработал forkJoinPool.invoke(htmlParser) !!!!!!!!!!!!!!");
//
//              forkJoinPool.shutdown();
//            if(forkJoinPool.isShutdown()){
//                Logger.getLogger(SiteForExecutorServiceRun.class.getName()).info("Вошли в условие  if(forkJoinPool.isShutdown())");
//                siteEntity.setStatus(StatusIndex.INDEXED);
//                siteEntity.setStatusTime(LocalDateTime.now());
//                indexServiceImp.getSiteService().saveSiteEntity(siteEntity);
//                Logger.getLogger(SiteForExecutorServiceRun.class.getName()).info("Проиндексировали сайт");
//            }
//
//        } catch (RuntimeException ex) {
//            siteEntity.setStatus(StatusIndex.FAILED);
//            siteEntity.setLastError(ex.getMessage());
//            siteEntity.setStatusTime(LocalDateTime.now());
//            indexServiceImp.getSiteService().saveSiteEntity(siteEntity);
//
//            Logger.getLogger(SiteForExecutorServiceRun.class.getName()).info("Поймали " + ex.getClass() + " из класса htmlParser");
//        }
//
//
//        /*siteEntityToSetString.setSiteEntity(siteEntity);
//        siteEntityToSetString.setStringSet(stringSetResult);*/
//
//        return stringSetResult;
//    }

    @Override
    public void run() {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        try {
            HtmlParser htmlParser = new HtmlParser(siteEntity.getUrl(), siteEntity, indexServiceImp);
            forkJoinPool.invoke(htmlParser);
            //  forkJoinPool.execute(htmlParser);


            //  Logger.getLogger(SiteForExecutorService.class.getName()).info("Пришел результат stringSetResult size() = " + stringSetResult.size());
            Logger.getLogger(SiteForExecutorServiceRun.class.getName()).info("Отработал forkJoinPool.invoke(htmlParser) !!!!!!!!!!!!!!");

            forkJoinPool.shutdown();
            if(forkJoinPool.isShutdown()){
                Logger.getLogger(SiteForExecutorServiceRun.class.getName()).info("Вошли в условие  if(forkJoinPool.isShutdown())");
                siteEntity.setStatus(StatusIndex.INDEXED);
                siteEntity.setStatusTime(LocalDateTime.now());
                indexServiceImp.getSiteService().saveSiteEntity(siteEntity);
                Logger.getLogger(SiteForExecutorServiceRun.class.getName()).info("Проиндексировали сайт");
            }

        } catch (RuntimeException ex) {
            siteEntity.setStatus(StatusIndex.FAILED);
            siteEntity.setLastError(ex.getMessage());
            siteEntity.setStatusTime(LocalDateTime.now());
            indexServiceImp.getSiteService().saveSiteEntity(siteEntity);

            Logger.getLogger(SiteForExecutorServiceRun.class.getName()).info("Поймали " + ex.getClass() + " из класса htmlParser");
        }
    }
}
