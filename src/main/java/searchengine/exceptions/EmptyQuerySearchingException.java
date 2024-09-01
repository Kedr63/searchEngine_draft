package searchengine.exceptions;

public class EmptyQuerySearchingException extends RuntimeException{

    public EmptyQuerySearchingException(String message) {
        super(message);
    }
}
