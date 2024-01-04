package searchengine.dto.indexing;

import lombok.Getter;

@Getter
public class IndexResponse {
    private boolean result;

    public IndexResponse(boolean result) {
        this.result = result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
