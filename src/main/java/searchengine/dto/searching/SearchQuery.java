package searchengine.dto.searching;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchQuery {
    String query;

    public SearchQuery() {
    }

    public SearchQuery(String query) {
        this.query = query;
    }
}
