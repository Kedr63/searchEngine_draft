package searchengine.services.indexService;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.dto.indexing.IndexingResponseError;
import searchengine.exceptions.IncompleteIndexingException;
import searchengine.exceptions.UtilityException;
import searchengine.services.utility.TimerExecution;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

@Getter
@Setter
public class UtilitiesIndexing {

    public volatile static boolean stopIndexing;
    public volatile static boolean indexingInProgress;
    public static boolean indexingSinglePage;

    public static final Object lockPageRepository = new Object();
    public static final Object lockLemmaRepository = new Object();
    public static final Object lockIndexLemmaService = new Object();
    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static void finishStartIndexing() {
        indexingInProgress = false;
        stopIndexing = false;
    }

    public static void finishIndexingSinglePage() {
        indexingSinglePage = false;
    }

    public static void startSinglePageIndexing() {
        indexingSinglePage = true;
    }


    public static IndexingResponse waitForCompleteStartIndexingAndTerminateStopIndexing() {
        while (indexingInProgress) { // ждем завершение метода startIndex()
            Logger.getLogger(IndexServiceImp.class.getName()).info("in loop while _onSpinWait");
            Thread.onSpinWait();
        }
        return new IndexingResponse(true);
    }


    /**
     * @note если в {@code HtmlRecursiveParser} выбросится {@code RuntimeException(ex)}
     * в методе {@code saveLastErrorInSiteEntity}, {@code ExecutorService} обернет {@code RuntimeException} в объект {@code Future}
     * и метод {@code get} выдаст исключение
     * и здесь поймаем как {@code ExecutionException} и обработаем ниже
     */
    public static List<IndexingResponse> getIndexingResponseListFromFutureList(List<Future<IndexingResponse>> futureList) {
        List<IndexingResponse> indexingResponseList = new ArrayList<>();
        IndexingResponse indexingResponse;
        for (Future<IndexingResponse> indexResponseFuture : futureList) {
            try {
                indexingResponse = indexResponseFuture.get();
                indexingResponseList.add(indexingResponse);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                indexingResponse = new IndexingResponseError(false, UtilityException.getShortMessageOfException(e));
                indexingResponseList.add(indexingResponse);
            }
        }
        return indexingResponseList;
    }

    public static IndexingResponse getTotalResultIndexingResponse(List<IndexingResponse> indexingResponseList) {
        int sizeList = indexingResponseList.size();
        double totalTime;

        for (IndexingResponse indexingResponse : indexingResponseList) {
            if (!indexingResponse.isResult()) {
                IndexingResponseError responseError = (IndexingResponseError) indexingResponse;
                String error = responseError.getError();
                throw new IncompleteIndexingException(error);

            }
        }
        totalTime = TimerExecution.computeTimeExecution();
        Logger.getLogger(IndexServiceImp.class.getName()).info("totalTime = " + totalTime);

        return indexingResponseList.get(sizeList - 1);
    }
}


