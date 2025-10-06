package searchengine.exceptions;

import org.jsoup.HttpStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import searchengine.dto.indexing.IndexingResponseError;
import searchengine.dto.indexing.IndexingResponse;

import java.util.Objects;
import java.util.logging.Logger;

@ControllerAdvice
public class DefaultAdvice {
    @ExceptionHandler(FailedSearchingException.class)
    public ResponseEntity<IndexingResponseError> handleException(FailedSearchingException ex) {
        IndexingResponseError response = new IndexingResponseError(false, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(IOException.class)
//    public ResponseEntity<IndexResponseError> handleException (IOException ex){
//        IndexResponseError responseError = new IndexResponseError(false, ex.getMessage());
//        Logger.getLogger(DefaultAdvice.class.getName()).info("в методе handleException (IOException ex)");
//        return new ResponseEntity<>(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
//    }

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
        return new ResponseEntity<>(responseError, HttpStatus.SEE_OTHER);
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
        Logger.getLogger(DefaultAdvice.class.getName()).info("в методе  handleException (IllegalMethodException)");
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

//    @ExceptionHandler(FailedGetLemmasException.class)
//    public ResponseEntity<IndexingResponseError> handleException (FailedGetLemmasException ex){
//        IndexingResponseError responseError = new IndexingResponseError(false, ex.getMessage());
//    }

}
