package searchengine.exceptions;

public class IncompleteIndexingException extends RuntimeException {

    public IncompleteIndexingException(String message) {
        super("Индексация не удалась: " + message);
    }
}
