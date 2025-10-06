package searchengine.exceptions;

public class FailedGetLemmasException extends RuntimeException{
    public FailedGetLemmasException(String message) {
        super("Не удалось получить леммы " + message);
    }

}
