package searchengine.services.indexService;

import lombok.Getter;
import lombok.Setter;
import searchengine.dto.indexing.IndexResponse;
import searchengine.dto.indexing.IndexResponseError;
import searchengine.model.SiteEntity;
import searchengine.model.StatusIndex;

import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;

@Getter
@Setter
public class SiteForExecutorServiceRun implements Callable<IndexResponse> {
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
    public IndexResponse call() {
        IndexResponse indexResponse = null;
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        try {
            HtmlParser parser = new HtmlParser(siteEntity.getUrl(), siteEntity, indexServiceImp);
            forkJoinPool.invoke(parser); // пока метод не отработает со всеми fork код дальше не пойдет

            if (parser.isDone() && siteEntity.getStatus().compareTo(StatusIndex.FAILED) != 0) {
                forkJoinPool.shutdown();
                siteEntity.setStatus(StatusIndex.INDEXED);
                Logger.getLogger(IndexServiceImp.class.getName()).info("siteEntity.setStatus(StatusIndex.INDEXED)");
                Logger.getLogger(IndexServiceImp.class.getName()).info("if (parser.isDone() && siteEntity.getStatus().compareTo(StatusIndex.FAILED) != 0)");
                indexServiceImp.getSiteService().saveSiteEntity(siteEntity);
                indexResponse = new IndexResponse(true);
            } else
                return new IndexResponseError(false, "error indexing");

//            forkJoinPool.shutdown();
//            if(forkJoinPool.isShutdown()){
//                Logger.getLogger(SiteForExecutorServiceRun.class.getName()).info("Вошли в условие  if(forkJoinPool.isShutdown())");
//                siteEntity.setStatus(StatusIndex.INDEXED);
//                siteEntity.setStatusTime(LocalDateTime.now());
//                indexServiceImp.getSiteService().saveSiteEntity(siteEntity);
//                Logger.getLogger(SiteForExecutorServiceRun.class.getName()).info("Проиндексировали сайт");
//            }

//        } catch (RuntimeException ex) {
//            siteEntity.setStatus(StatusIndex.FAILED);
//            siteEntity.setLastError(ex.getMessage());
//            siteEntity.setStatusTime(LocalDateTime.now());
//            indexServiceImp.getSiteService().saveSiteEntity(siteEntity);
//
//            Logger.getLogger(SiteForExecutorServiceRun.class.getName()).info("Поймали " + ex.getClass() + " из класса htmlParser");
//        }
        } catch (Exception e) {
            forkJoinPool.shutdown();
//            siteEntity.setStatus(StatusIndex.FAILED);
//            siteEntity.setLastError(e.getMessage());
            Logger.getLogger(SiteForExecutorServiceRun.class.getName()).info("поймали  - " + e.getMessage());

            // indexServiceImp.getSiteService().saveSiteEntity(siteEntity);
         //   indexServiceImp.getSiteService().saveSiteEntity(siteEntity);

            indexResponse = new IndexResponseError(false, siteEntity.getLastError());
        }
        return indexResponse;
    }
}
