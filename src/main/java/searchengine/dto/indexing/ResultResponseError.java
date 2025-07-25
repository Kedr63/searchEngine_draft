package searchengine.dto.indexing;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultResponseError extends IndexingResponse {
    private String error;

    public ResultResponseError(boolean result, String error) {
        super(result);
        this.error = error;
    }
}
