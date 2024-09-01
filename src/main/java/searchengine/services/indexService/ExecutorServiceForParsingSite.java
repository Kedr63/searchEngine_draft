package searchengine.services.indexService;

import lombok.Getter;
import lombok.Setter;
import searchengine.dto.ResultResponseError;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.model.SiteEntity;
import searchengine.model.StatusIndex;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;

@Getter
@Setter
public class ExecutorServiceForParsingSite implements Callable<IndexingResponse> {
    // private Site url;
    private SiteEntity siteEntity;
    private PoolService poolService;

    public ExecutorServiceForParsingSite(SiteEntity siteEntity, PoolService poolService) {
        this.siteEntity = siteEntity;
        this.poolService = poolService;
    }

    @Override
    public IndexingResponse call() {
        IndexingResponse indexingResponseForFuture = null;
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        //   try {
        HtmlParser htmlParser  = new HtmlParser(siteEntity.getUrl(), siteEntity, poolService);
        forkJoinPool.invoke(htmlParser); // пока метод не отработает со всеми fork код дальше не пойдет

        if (htmlParser.isDone() && siteEntity.getStatus().compareTo(StatusIndex.FAILED) != 0 && !UtilitiesIndexing.stopStartIndexingMethod) {
            siteEntity.setStatus(StatusIndex.INDEXED);
            siteEntity.setStatusTime(LocalDateTime.now());
            Logger.getLogger(IndexServiceImp.class.getName()).info("siteEntity.setStatus(StatusIndex.INDEXED)");
            poolService.getSiteService().saveSiteEntity(siteEntity);

            indexingResponseForFuture = new IndexingResponse(true);
        }
        if (htmlParser.isDone() && UtilitiesIndexing.stopStartIndexingMethod) {
            siteEntity.setStatus(StatusIndex.FAILED);
            siteEntity.setLastError("Индексация остановлена пользователем");
            poolService.getSiteService().saveSiteEntity(siteEntity);
            Logger.getLogger(IndexServiceImp.class.getName()).info("siteEntity.setLastError(\"Индексация остановлена пользователем\");");

           // indexingResponseForFuture = new IndexingResponse(false);
            indexingResponseForFuture = new ResultResponseError(false, siteEntity.getLastError());
        }

        forkJoinPool.shutdown();
       // futureIndexingResponse = new IndexingResponse(true);

        return indexingResponseForFuture;
    }
}
