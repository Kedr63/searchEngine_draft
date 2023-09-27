package searchengine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import searchengine.dto.indexing.IndexResponseError;

@ControllerAdvice
public class DefaultAdvice {
    @ExceptionHandler(FailedSearching.class)
    public ResponseEntity<IndexResponseError> handleException(FailedSearching ex) {
        IndexResponseError response = new IndexResponseError(false, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
