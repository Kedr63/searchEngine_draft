package searchengine.services.serviceIndex;

import lombok.Getter;
import lombok.Setter;
import searchengine.config.Site;
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
public class SiteForExecutorService implements Callable<SiteEntity> {
    // private Site url;
    private SiteEntity siteEntity;

    private IndexServiceImp indexServiceImp;

    public SiteForExecutorService(SiteEntity siteEntity, IndexServiceImp indexServiceImp) {
        //  this.url = url;
        this.siteEntity = siteEntity;
        this.indexServiceImp = indexServiceImp;
    }

    @Override
    public SiteEntity call() throws Exception {
        Set<String> stringSetResult = new HashSet<>();
        //  SiteEntityToSetString siteEntityToSetString = new SiteEntityToSetString();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        try {
            HtmlParser htmlParser = new HtmlParser(siteEntity.getUrl(), siteEntity, indexServiceImp);
            forkJoinPool.invoke(htmlParser);

            Logger.getLogger(SiteForExecutorService.class.getName()).info("Отработал forkJoinPool.invoke(htmlParser) !!!!!!!!!!!!!!");

            forkJoinPool.shutdown();
            if(forkJoinPool.isShutdown()){
                siteEntity.setStatus(StatusIndex.INDEXED);
                siteEntity.setStatusTime(LocalDateTime.now());
                indexServiceImp.getSiteRepository().save(siteEntity);
                Logger.getLogger(SiteForExecutorService.class.getName()).info("Проиндексировали сайт");
            }

        } catch (RuntimeException ex) {
            siteEntity.setStatus(StatusIndex.FAILED);
            siteEntity.setLastError(ex.getMessage());
            siteEntity.setStatusTime(LocalDateTime.now());
            indexServiceImp.getSiteRepository().save(siteEntity);

            Logger.getLogger(SiteForExecutorService.class.getName()).info("Поймали " + ex.getClass() + " из класса htmlParser");
        }


        /*siteEntityToSetString.setSiteEntity(siteEntity);
        siteEntityToSetString.setStringSet(stringSetResult);*/

        return siteEntity;
    }
}
