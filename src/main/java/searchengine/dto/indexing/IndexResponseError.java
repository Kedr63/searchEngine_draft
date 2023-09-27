package searchengine.dto.indexing;

public class IndexResponseError extends IndexResponse{

    private String error;

    public IndexResponseError(boolean result, String error) {
        super(result);
        this.error=error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
