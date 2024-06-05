package searchengine.exceptions;

public class NoSuchSiteException extends RuntimeException {
    public NoSuchSiteException(String message) {
        super(message);
    }
}
