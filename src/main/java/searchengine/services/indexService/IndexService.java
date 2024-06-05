package searchengine.services.indexService;

import org.springframework.http.ResponseEntity;
import searchengine.dto.indexing.IndexResponse;

public interface IndexService {

    ResponseEntity<IndexResponse> startIndexing();

    ResponseEntity<IndexResponse> stopIndexing();

    ResponseEntity<IndexResponse> indexPage(String page);



    //   public IndexResponseError throwsException() throws IOException;
}
