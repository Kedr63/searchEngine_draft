package searchengine.services.indexService;

import searchengine.dto.dtoToBD.PageDtoSingle;
import searchengine.dto.indexing.IndexingResponse;


public interface IndexService {

    IndexingResponse startIndexing();

    IndexingResponse stopIndexing();

    IndexingResponse indexSinglePage(PageDtoSingle pageDtoSingle);

}
