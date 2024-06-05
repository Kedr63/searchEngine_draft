package searchengine.services.indexService;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import searchengine.dto.indexing.IndexResponse;

import java.util.logging.Logger;

@Getter
@Setter
public class UtilitiesIndexing {

    public volatile static boolean stopStartIndexing;
    public volatile static boolean executeStartIndexing;
    public static boolean computeIndexingSinglePage;

    public static void isDoneStartIndexing(){
        executeStartIndexing = false;
        stopStartIndexing = false;
    }

    public static void isDoneIndexingSinglePage(){
        computeIndexingSinglePage = false;
    }

    public static void isStartLaunchPageIndexing(){
        computeIndexingSinglePage = true;
    }


    public static ResponseEntity<IndexResponse> waitForCompleteStartIndexingAndTerminateStopIndexing(){
        while (executeStartIndexing){ // ждем завершение метода startIndex()
            Logger.getLogger(IndexServiceImp.class.getName()).info("in loop while _onSpinWait");
            Thread.onSpinWait();
        }
        Logger.getLogger(IndexServiceImp.class.getName()).info("перед методом - throw new StopThreadException");
        return new ResponseEntity<IndexResponse>(new IndexResponse(true), HttpStatus.GONE);
      //  throw new StopThreadException();
    }

    public static final Object lockPageRepository = new Object();

    public static final Object lockLemmaRepository = new Object();

    public static  final Object lockIndexLemmaService = new Object();
}
