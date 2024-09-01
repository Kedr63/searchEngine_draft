package searchengine.services.searchService;

import searchengine.dto.searching.SearchQuery;
import searchengine.dto.searching.SearchingResponse;

public interface SearchService {

    SearchingResponse search(SearchQuery query);
}
