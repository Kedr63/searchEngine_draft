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

    public volatile static boolean stopStartIndexingMethod;
    public volatile static boolean indexingInProgress;
    public static boolean indexingSinglePage;

    public static final Object lockPageRepository = new Object();
    public static final Object lockLemmaRepository = new Object();
    public static  final Object lockIndexLemmaService = new Object();
    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static void isDoneStartIndexing(){
        indexingInProgress = false;
        stopStartIndexingMethod = false;
    }

    public static void isDoneIndexingSinglePage(){
        indexingSinglePage = false;
    }

    public static void isStartSinglePageIndexing(){
        indexingSinglePage = true;
    }


    public static IndexingResponse waitForCompleteStartIndexingAndTerminateStopIndexing(){
        while (indexingInProgress){ // ждем завершение метода startIndex()
            Logger.getLogger(IndexServiceImp.class.getName()).info("in loop while _onSpinWait");
            Thread.onSpinWait();
        }
        return new IndexingResponse(true);
    }


    /**
     * @note  если в {@code HtmlRecursiveParser} выбросится {@code RuntimeException(ex)}
     *        в методе {@code saveLastErrorInSiteEntity}, {@code ExecutorService} обернет {@code RuntimeException} в объект {@code Future}
     *        и метод {@code get} выдаст исключение
     *        и здесь поймаем как {@code ExecutionException e} и обработаем ниже */
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
        boolean hasIndexingResponseWithValueFalse = false;
        String error = "";

        if (!indexingResponseList.isEmpty()) {

            for (IndexingResponse indexingResponse : indexingResponseList) {
                if (!indexingResponse.isResult()) {
                    hasIndexingResponseWithValueFalse = true;
                    IndexingResponseError responseError = (IndexingResponseError) indexingResponse;
                    error = responseError.getError();
                }
            }

            double totalTime;
            UtilitiesIndexing.isDoneStartIndexing();
            totalTime = TimerExecution.computeTimeExecution();
            if (hasIndexingResponseWithValueFalse) {
                throw new IncompleteIndexingException(error);
            } else {
                Logger.getLogger(IndexServiceImp.class.getName()).info("totalTime = " + totalTime);
                return indexingResponseList.get(sizeList - 1);
            }

        }
        else {
            double totalTime = TimerExecution.computeTimeExecution();
            Logger.getLogger(IndexServiceImp.class.getName()).info("totalTime = " + totalTime);
            return new IndexingResponseError(false, "что то пошло не так");
        }
    }


}


