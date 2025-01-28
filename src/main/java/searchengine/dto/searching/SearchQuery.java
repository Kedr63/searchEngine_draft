package searchengine.dto.searching;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchQuery {
    private String query;
    private int offset;
    private int limit;
    private String site;

    // поля взял из данных формы \Search\ браузера. Посмотрел в инспекторе в разделе сеть

    public SearchQuery() {
    }

    public SearchQuery(String query) {
        this.query = query;
    }

    public SearchQuery(String query, int offset, int limit) {
        this.query = query;
        this.offset = offset;
        this.limit = limit;
    }

    public SearchQuery(String query, int offset, int limit, String site) {
        this.query = query;
        this.offset = offset;
        this.limit = limit;
        this.site = site;
    }
}
