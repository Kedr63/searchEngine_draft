package searchengine.dto;

import lombok.Getter;
import lombok.Setter;
import searchengine.dto.indexing.IndexingResponse;

@Getter
@Setter
public class ResultResponseError extends IndexingResponse {
    private String error;

    public ResultResponseError(boolean result, String error) {
        super(result);
        this.error = error;
    }
}
