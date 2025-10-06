package searchengine.services.indexService;

import searchengine.dto.indexing.IndexingResponse;


public interface IndexService {

    IndexingResponse startIndexing();

    IndexingResponse stopIndexing();

    IndexingResponse indexSinglePage(String pageUrl);

}
