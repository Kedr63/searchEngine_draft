package searchengine.exceptions;

import org.jsoup.HttpStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import searchengine.dto.indexing.IndexResponse;
import searchengine.dto.indexing.IndexResponseError;

import java.util.Objects;
import java.util.logging.Logger;

@ControllerAdvice
public class DefaultAdvice {
    @ExceptionHandler(FailedSearching.class)
    public ResponseEntity<IndexResponseError> handleException(FailedSearching ex) {
        IndexResponseError response = new IndexResponseError(false, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(IOException.class)
//    public ResponseEntity<IndexResponseError> handleException (IOException ex){
//        IndexResponseError responseError = new IndexResponseError(false, ex.getMessage());
//        Logger.getLogger(DefaultAdvice.class.getName()).info("в методе handleException (IOException ex)");
//        return new ResponseEntity<>(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    @ExceptionHandler(HttpFailedConnectionException.class)
    public ResponseEntity<IndexResponseError> handleException (HttpFailedConnectionException ex, int codeError){
        IndexResponseError indexResponseError = new IndexResponseError(false, ex.getMessage());
        Logger.getLogger(DefaultAdvice.class.getName()).info("в методе  handleException (HttpFailedConnectionException ex)");
            return new ResponseEntity<>(indexResponseError, HttpStatus.valueOf(codeError));
    }

    @ExceptionHandler(FailedConnectionException.class)
    public ResponseEntity<IndexResponseError> handleException (FailedConnectionException ex){
        IndexResponseError indexResponseError = new IndexResponseError(false, UtilityException.getShortMessageOfException(ex));
        Logger.getLogger(DefaultAdvice.class.getName()).info("в методе  handleException (FailedConnectionException ex)");
        return new ResponseEntity<>(indexResponseError, HttpStatus.SEE_OTHER);
    }

    @ExceptionHandler(HttpStatusException.class)
    public ResponseEntity<IndexResponseError> handleException (HttpStatusException ex){
        IndexResponseError indexResponseError = new IndexResponseError(false, ex.getMessage() + "httpStatus error " + ex.getStatusCode());
        Logger.getLogger(DefaultAdvice.class.getName()).info("в методе  handleException (HttpStatusException ex)");
        return new ResponseEntity<>(indexResponseError, Objects.requireNonNull(HttpStatus.resolve(ex.getStatusCode())));
    }

    @ExceptionHandler(StopThreadException.class)
    public ResponseEntity<IndexResponse> handleException (StopThreadException ex){
        IndexResponse indexResponse = new IndexResponse(true);
        Logger.getLogger(DefaultAdvice.class.getName()).info("в методе  handleException handleException (StopThreadException ex)");
        return new ResponseEntity<>(indexResponse, HttpStatus.SEE_OTHER);
    }

    @ExceptionHandler(IllegalMethodException.class)
    public ResponseEntity<IndexResponseError> handleException (IllegalMethodException ex){
        IndexResponseError indexResponseError = new IndexResponseError(false, ex.getMessage());
        Logger.getLogger(DefaultAdvice.class.getName()).info("в методе  handleException (IllegalMethodException)");
        return new ResponseEntity<>(indexResponseError, HttpStatus.FORBIDDEN);

    }

    @ExceptionHandler(NoSuchSiteException.class)
    public ResponseEntity<IndexResponseError> handleException (NoSuchSiteException ex){
        IndexResponseError indexResponseError = new IndexResponseError(false, ex.getMessage());
        return new ResponseEntity<>(indexResponseError, HttpStatus.NOT_FOUND);
    }



}
