package searchengine.exceptions;

public class HttpFailedConnectionException extends RuntimeException{

    public HttpFailedConnectionException(String message) {
        super(message);
    }

    public HttpFailedConnectionException(String message, int statusCode) {
        super(message);
    }

}
