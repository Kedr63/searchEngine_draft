package searchengine.exceptions;

import org.jsoup.HttpStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import searchengine.dto.indexing.IndexingResponseError;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.dto.searching.SearchingResponse;
import searchengine.dto.searching.SearchingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@ControllerAdvice
public class DefaultAdvice {
    @ExceptionHandler(FailedSearchingException.class)
    public ResponseEntity<IndexingResponseError> handleException(FailedSearchingException ex) {
        IndexingResponseError response = new IndexingResponseError(false, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpFailedConnectionException.class)
    public ResponseEntity<IndexingResponseError> handleException (HttpFailedConnectionException ex, int codeError){
        IndexingResponseError responseError = new IndexingResponseError(false, ex.getMessage());
        Logger.getLogger(DefaultAdvice.class.getName()).info("в методе  handleException (HttpFailedConnectionException ex)");
            return new ResponseEntity<>(responseError, HttpStatus.valueOf(codeError));
    }

    @ExceptionHandler(FailedConnectionException.class)
    public ResponseEntity<IndexingResponseError> handleException (FailedConnectionException ex){
        IndexingResponseError responseError = new IndexingResponseError(false, UtilityException.getShortMessageOfException(ex));
        Logger.getLogger(DefaultAdvice.class.getName()).info("в методе  handleException (FailedConnectionException ex)");
        return new ResponseEntity<>(responseError, HttpStatus.GATEWAY_TIMEOUT);
    }

    @ExceptionHandler(HttpStatusException.class)
    public ResponseEntity<IndexingResponseError> handleException (HttpStatusException ex){
        IndexingResponseError responseError = new IndexingResponseError(false, ex.getMessage() + "httpStatus error " + ex.getStatusCode());
        Logger.getLogger(DefaultAdvice.class.getName()).info("в методе  handleException (HttpStatusException ex)");
        return new ResponseEntity<>(responseError, Objects.requireNonNull(HttpStatus.resolve(ex.getStatusCode())));
    }

    @ExceptionHandler(StopThreadException.class)
    public ResponseEntity<IndexingResponse> handleException (StopThreadException ex){
        IndexingResponse indexingResponse = new IndexingResponse(true);
        Logger.getLogger(DefaultAdvice.class.getName()).info("в методе  handleException handleException (StopThreadException ex)");
        return new ResponseEntity<>(indexingResponse, HttpStatus.SEE_OTHER);
    }


    @ExceptionHandler(IllegalMethodException.class)
    public ResponseEntity<IndexingResponseError> handleException (IllegalMethodException ex){
        IndexingResponseError responseError = new IndexingResponseError(false, ex.getMessage());
        return new ResponseEntity<>(responseError, HttpStatus.FORBIDDEN);

    }

    @ExceptionHandler(NoSuchSiteException.class)
    public ResponseEntity<IndexingResponseError> handleException (NoSuchSiteException ex){
        IndexingResponseError responseError = new IndexingResponseError(false, ex.getMessage());
        return new ResponseEntity<>(responseError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmptyQuerySearchingException.class)
    public ResponseEntity<IndexingResponseError> handleException (EmptyQuerySearchingException ex){
        IndexingResponseError responseError = new IndexingResponseError(false, ex.getMessage());
        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IncompleteIndexingException.class)
    public ResponseEntity<IndexingResponseError> handleException (IncompleteIndexingException ex){
        IndexingResponseError responseError = new IndexingResponseError(false, ex.getMessage());
        return new ResponseEntity<>(responseError, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(NoSuchLemmaForSearchingInContentException.class)
    public ResponseEntity<IndexingResponseError> handleException (NoSuchLemmaForSearchingInContentException ex){
        IndexingResponseError responseError = new IndexingResponseError(false, ex.getMessage());
        return new ResponseEntity<>(responseError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoPagesForSearchingException.class)
    public ResponseEntity<SearchingResponse> handleException (NoPagesForSearchingException ex){
        SearchingResponse searchingResponse = new SearchingResponse();
        searchingResponse.setCount(0);
        searchingResponse.setResult(true);
        List<SearchingResult> searchingResultList = new ArrayList<>();
        searchingResponse.setData(searchingResultList);
        return new ResponseEntity<>(searchingResponse, HttpStatus.NOT_FOUND);
    }

//    @ExceptionHandler(FailedGetLemmasException.class)
//    public ResponseEntity<IndexingResponseError> handleException (FailedGetLemmasException ex){
//        IndexingResponseError responseError = new IndexingResponseError(false, ex.getMessage());
//    }

}
