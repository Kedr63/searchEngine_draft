package searchengine.dto.indexing;

public class IndexResponse {
    private boolean result;

    public IndexResponse(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
