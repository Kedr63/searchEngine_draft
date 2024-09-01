package searchengine.dto.indexing;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IndexingResponse {
    private boolean result;

    public IndexingResponse(boolean result) {
        this.result = result;
    }
}
