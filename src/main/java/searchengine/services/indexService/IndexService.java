package searchengine.services.indexService;

import searchengine.dto.indexing.IndexingResponse;

public interface IndexService {

    IndexingResponse startIndexing();

    IndexingResponse stopIndexing();

    IndexingResponse indexSinglePage(String page);



    //   public IndexResponseError throwsException() throws IOException;
}
