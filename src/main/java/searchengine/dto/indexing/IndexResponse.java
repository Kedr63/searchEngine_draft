package searchengine.dto.indexing;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndexResponse {
    private boolean result;

    public IndexResponse(boolean result) {
        this.result = result;
    }

    public IndexResponse() {
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
