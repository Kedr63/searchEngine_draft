package searchengine.dto.searching;

import lombok.Data;

import javax.naming.directory.SearchResult;
import java.util.List;

@Data
public class SearchingResponse {
    private boolean result;
    private int count;
    private List<SearchResult> data;
}
