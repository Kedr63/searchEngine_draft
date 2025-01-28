package searchengine.exceptions;

public class NoSuchLemmaForSearchingInContentException extends RuntimeException {
    public NoSuchLemmaForSearchingInContentException(String message) {
        super(message);
    }
}
