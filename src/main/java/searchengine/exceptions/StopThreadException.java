package searchengine.exceptions;

public class StopThreadException extends RuntimeException{

    public StopThreadException() {
    }

    public StopThreadException(String message) {
        super(message);
    }
}
