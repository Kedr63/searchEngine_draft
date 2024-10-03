package searchengine.services.indexService;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import searchengine.dto.indexing.IndexingResponse;

import java.util.logging.Logger;

@Getter
@Setter
public class UtilitiesIndexing {

    public volatile static boolean stopStartIndexingMethod;
    public volatile static boolean indexingInProgress;
    public static boolean computeIndexingSinglePage;

    public static void isDoneStartIndexing(){
        indexingInProgress = false;
        stopStartIndexingMethod = false;
    }

    public static void isDoneIndexingSinglePage(){
        computeIndexingSinglePage = false;
    }

    public static void isStartSinglePageIndexing(){
        computeIndexingSinglePage = true;
    }


    public static IndexingResponse waitForCompleteStartIndexingAndTerminateStopIndexing(){
        while (indexingInProgress){ // ждем завершение метода startIndex()
            Logger.getLogger(IndexServiceImp.class.getName()).info("in loop while _onSpinWait");
            Thread.onSpinWait();
        }
        return new IndexingResponse(true);
    }



    public static final Object lockPageRepository = new Object();

    public static final Object lockLemmaRepository = new Object();

    public static  final Object lockIndexLemmaService = new Object();


    public static final ObjectMapper objectMapper = new ObjectMapper();
}
