package searchengine.services.indexService;

import lombok.Getter;
import lombok.Setter;
import searchengine.config.ErrorMessageConfig;
import searchengine.dto.dtoToBD.SiteDto;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.dto.indexing.IndexingResponseError;
import searchengine.model.StatusIndex;
import searchengine.services.PoolService;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

/**
 * Реализует интерфейс {@code Callable<T>}, где T — тип возвращаемого значения.
 * Передаем нашу реализацию в метод {@code submit()} объекта {@code ExecutorService} в классе {@code IndexServiceImp}.
 * Полученный объект {@code Future<T>} позволяет ожидать завершение задания и получать результат.
 * Когда вызываем метод {@code future.get()}, программа останавливается и ждет, пока задача не завершит свою работу и не вернет результат.
 * Закрываем ресурс (ExecutorService) после окончания работы - {@code executorService.shutdown()}.
 * @Note Класс {@code Callable} используется в Java для реализации заданий, которые возвращают значение после завершения своей работы.
 * Для управления такими заданиями используются исполнители {@code ExecutorService}
 * */
@Getter
@Setter
public class TaskForkJoinPoolForParsingSite implements Callable<IndexingResponse> {

    private SiteDto siteDto;
    private final PoolService poolService;
    private final ErrorMessageConfig errorMessageConfig;

    public TaskForkJoinPoolForParsingSite(SiteDto siteDto, PoolService poolService, ErrorMessageConfig errorMessageConfig) {
        this.siteDto = siteDto;
        this.poolService = poolService;
        this.errorMessageConfig = errorMessageConfig;
    }

    @Override
    public IndexingResponse call() {
        IndexingResponse indexingResponseForFuture = null;
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        HtmlRecursiveParser htmlRecursiveParser = new HtmlRecursiveParser(siteDto.getUrl(), siteDto, poolService);
        forkJoinPool.invoke(htmlRecursiveParser); // пока метод не отработает со всеми fork, код дальше не пойдет

        if (htmlRecursiveParser.isDone() && siteDto.getStatusIndex().compareTo(StatusIndex.FAILED) != 0 && !UtilitiesIndexing.stopIndexing) {
            siteDto.setStatusIndex(StatusIndex.INDEXED);
            siteDto.setStatusTime(LocalDateTime.now());
            poolService.getSiteService().saveSiteDto(siteDto);
            indexingResponseForFuture = new IndexingResponse(true);
        }
        if (htmlRecursiveParser.isDone() && UtilitiesIndexing.stopIndexing) {
            siteDto.setStatusIndex(StatusIndex.FAILED);
            siteDto.setLastError(errorMessageConfig.getErrorIndexingStopUser());
            poolService.getSiteService().saveSiteDto(siteDto);
            indexingResponseForFuture = new IndexingResponseError(false, siteDto.getLastError());
        }

        forkJoinPool.shutdown();

        return indexingResponseForFuture;
    }
}
