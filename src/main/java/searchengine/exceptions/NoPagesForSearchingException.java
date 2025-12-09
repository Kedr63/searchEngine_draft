package searchengine.exceptions;

public class NoPagesForSearchingException extends RuntimeException {
    public NoPagesForSearchingException(String message) {
        super(message);
    }
}
