package searchengine.services.indexService;

import searchengine.dto.indexing.IndexResponse;
import searchengine.dto.indexing.IndexResponseError;

public interface IndexService {

    IndexResponse startIndexing();

    IndexResponseError stopIndexing() throws InterruptedException;

 //   public IndexResponseError throwsException() throws IOException;
}
