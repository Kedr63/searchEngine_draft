package searchengine.services.indexService;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import searchengine.dto.indexing.IndexResponse;
import searchengine.model.SiteEntity;
import searchengine.model.StatusIndex;

import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;

@Getter
@Setter
public class ExecutorServiceForParsingSiteEntity implements Callable<ResponseEntity<IndexResponse>> {
    // private Site url;
    private SiteEntity siteEntity;
    private PoolService poolService;

    public ExecutorServiceForParsingSiteEntity(SiteEntity siteEntity, PoolService poolService) {
        this.siteEntity = siteEntity;
        this.poolService = poolService;
    }

    @Override
    public ResponseEntity<IndexResponse> call() throws InterruptedException {
        ResponseEntity<IndexResponse> futureResponseEntity = null;
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        //   try {
        HtmlParser parser = new HtmlParser(siteEntity.getUrl(), siteEntity, poolService);
        forkJoinPool.invoke(parser); // пока метод не отработает со всеми fork код дальше не пойдет

        if (parser.isDone() && siteEntity.getStatus().compareTo(StatusIndex.FAILED) != 0 && !UtilitiesIndexing.stopStartIndexing) {
            siteEntity.setStatus(StatusIndex.INDEXED);
            Logger.getLogger(IndexServiceImp.class.getName()).info("siteEntity.setStatus(StatusIndex.INDEXED)");
            poolService.getSiteService().saveSiteEntity(siteEntity);
        }
        if (parser.isDone() && UtilitiesIndexing.stopStartIndexing) {
            siteEntity.setStatus(StatusIndex.FAILED);
            siteEntity.setLastError("Индексация остановлена пользователем");
            poolService.getSiteService().saveSiteEntity(siteEntity);
            Logger.getLogger(IndexServiceImp.class.getName()).info("siteEntity.setLastError(\"Индексация остановлена пользователем\");");
        }

        forkJoinPool.shutdown();
        futureResponseEntity = new ResponseEntity<>(new IndexResponse(true), HttpStatus.OK);

        return futureResponseEntity;
    }
}
