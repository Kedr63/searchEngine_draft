package searchengine.services.indexService;

import lombok.Getter;
import lombok.Setter;
import searchengine.model.SiteEntity;
import searchengine.model.StatusIndex;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;

@Getter
@Setter
public class SiteForExecutorService implements Callable<Set<String>> {

    private SiteEntity siteEntity;
    private final PoolService poolService;

    public SiteForExecutorService(SiteEntity siteEntity, PoolService poolService) {
        this.siteEntity = siteEntity;
        this.poolService = poolService;
    }

    @Override
    public Set<String> call() throws Exception {
        Set<String> stringSetResult = new HashSet<>();
        //  SiteEntityToSetString siteEntityToSetString = new SiteEntityToSetString();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        try {
            HtmlParser htmlParser = new HtmlParser(siteEntity.getUrl(), siteEntity, poolService);
            forkJoinPool.invoke(htmlParser);
          //  forkJoinPool.execute(htmlParser);


          //  Logger.getLogger(SiteForExecutorService.class.getName()).info("Пришел результат stringSetResult size() = " + stringSetResult.size());
            Logger.getLogger(SiteForExecutorService.class.getName()).info("Отработал forkJoinPool.invoke(htmlParser) !!!!!!!!!!!!!!");

              forkJoinPool.shutdown();
            if(forkJoinPool.isShutdown()){
                Logger.getLogger(SiteForExecutorService.class.getName()).info("Вошли в условие  if(forkJoinPool.isShutdown())");
                siteEntity.setStatus(StatusIndex.INDEXED);
                siteEntity.setStatusTime(LocalDateTime.now());
                poolService.getSiteService().saveSiteEntity(siteEntity);
                Logger.getLogger(SiteForExecutorService.class.getName()).info("Проиндексировали сайт");
            }

        } catch (RuntimeException ex) {
            siteEntity.setStatus(StatusIndex.FAILED);
            siteEntity.setLastError(ex.getMessage());
            siteEntity.setStatusTime(LocalDateTime.now());
            poolService.getSiteService().saveSiteEntity(siteEntity);

            Logger.getLogger(SiteForExecutorService.class.getName()).info("Поймали " + ex.getClass() + " из класса htmlParser");
        }


        /*siteEntityToSetString.setSiteEntity(siteEntity);
        siteEntityToSetString.setStringSet(stringSetResult);*/

        return stringSetResult;
    }
}
