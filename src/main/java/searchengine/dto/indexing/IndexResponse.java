package searchengine.dto.indexing;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IndexResponse {
    private boolean result;

    public IndexResponse(boolean result) {
        this.result = result;
    }

    public IndexResponse() {
    }

}
