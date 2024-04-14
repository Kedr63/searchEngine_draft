package searchengine.dto.indexing;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import searchengine.services.indexService.IndexServiceImp;

import java.util.logging.Logger;

@Getter
@Setter
public class UtilitiesIndexing {

    public volatile static boolean stopStartIndexing;
    public volatile static boolean executionOfMethodStartIndexing;

    public static void isDoneStartIndexing(){
        executionOfMethodStartIndexing = false;
    }

    public static ResponseEntity<IndexResponse> waitForCompleteStartIndexingAndTerminateStopIndexing(){
        while (executionOfMethodStartIndexing){
            Logger.getLogger(IndexServiceImp.class.getName()).info("in loop while _onSpinWait");
            Thread.onSpinWait();
        }
        Logger.getLogger(IndexServiceImp.class.getName()).info("перед методом - throw new StopThreadException");
        return new ResponseEntity<IndexResponse>(new IndexResponse(true), HttpStatus.GONE);
      //  throw new StopThreadException();
    }
}
