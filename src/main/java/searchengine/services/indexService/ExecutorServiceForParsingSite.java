package searchengine.services.indexService;

import lombok.Getter;
import lombok.Setter;
import searchengine.dto.indexing.ResultResponseError;
import searchengine.dto.dtoToBD.SiteDto;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.model.StatusIndex;
import searchengine.services.PoolService;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;

@Getter
@Setter
public class ExecutorServiceForParsingSite implements Callable<IndexingResponse> {
    // private Site url;
    private SiteDto siteDto;
    private PoolService poolService;

    public ExecutorServiceForParsingSite(SiteDto siteDto, PoolService poolService) {
        this.siteDto = siteDto;
        this.poolService = poolService;
    }

    @Override
    public IndexingResponse call() {
        IndexingResponse indexingResponseForFuture = null;
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        //   try {
        HtmlParser htmlParser  = new HtmlParser(siteDto.getUrl(), siteDto, poolService);
        forkJoinPool.invoke(htmlParser); // пока метод не отработает со всеми fork код дальше не пойдет

        if (htmlParser.isDone() && siteDto.getStatusIndex().compareTo(StatusIndex.FAILED) != 0 && !UtilitiesIndexing.stopStartIndexingMethod) {
            siteDto.setStatusIndex(StatusIndex.INDEXED);
            siteDto.setStatusTime(LocalDateTime.now());
            Logger.getLogger(IndexServiceImp.class.getName()).info("siteEntity.setStatus(StatusIndex.INDEXED)");
            poolService.getSiteService().saveSiteDto(siteDto);

            indexingResponseForFuture = new IndexingResponse(true);
        }
        if (htmlParser.isDone() && UtilitiesIndexing.stopStartIndexingMethod) {
            siteDto.setStatusIndex(StatusIndex.FAILED);
            siteDto.setLastError("Индексация остановлена пользователем");
            poolService.getSiteService().saveSiteDto(siteDto);
            Logger.getLogger(IndexServiceImp.class.getName()).info("siteEntity.setLastError(\"Индексация остановлена пользователем\");");

           // indexingResponseForFuture = new IndexingResponse(false);
            indexingResponseForFuture = new ResultResponseError(false, siteDto.getLastError());
        }

        forkJoinPool.shutdown();
       // futureIndexingResponse = new IndexingResponse(true);

        return indexingResponseForFuture;
    }
}
