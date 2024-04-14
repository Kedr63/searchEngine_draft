package searchengine.exceptions;

public class FailedConnectionException extends RuntimeException{


    public FailedConnectionException(String message) {
        super(message);
    }

    public FailedConnectionException(Throwable cause) {
        super(cause);
    }
}
