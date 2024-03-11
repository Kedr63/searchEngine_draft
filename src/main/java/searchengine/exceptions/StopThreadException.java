package searchengine.exceptions;

public class StopThreadException extends Exception{
    private String message;

    public StopThreadException(String message) {
        this.message = message;
    }


}
