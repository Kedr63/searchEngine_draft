package searchengine.dto.searching;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchingResponse {
    private boolean result;
    private int count;
    private List<SearchingResult> data;

    public SearchingResponse() {
    }

    public SearchingResponse(boolean result, int count, List<SearchingResult> data) {
        this.result = result;
        this.count = count;
        this.data = data;
    }
}
