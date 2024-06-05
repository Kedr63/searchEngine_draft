package searchengine.services.searchService;

import org.springframework.http.ResponseEntity;
import searchengine.dto.searching.SearchResult;

public interface SearchService {


    ResponseEntity<SearchResult> search(String query);
}
