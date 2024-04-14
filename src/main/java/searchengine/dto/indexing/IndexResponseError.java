package searchengine.dto.indexing;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IndexResponseError extends IndexResponse{

    private String error;
  //  private int codeError;

    public IndexResponseError() {
    }

    public IndexResponseError(boolean result, String error) {
        super(result);
        this.error=error;
    }
}
