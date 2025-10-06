package searchengine.dto.indexing;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndexingResponseError extends IndexingResponse {
    private String error;

    public IndexingResponseError(boolean result, String error) {
        super(result);
        this.error = error;
    }
}
