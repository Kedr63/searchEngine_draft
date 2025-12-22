package searchengine.exceptions;

public class FailedConnectionException extends RuntimeException{


    public FailedConnectionException(String message) {
        super("Что-то с соединением ( :" + message);
    }

    public FailedConnectionException(Throwable cause) {
        super(cause);
    }
}
