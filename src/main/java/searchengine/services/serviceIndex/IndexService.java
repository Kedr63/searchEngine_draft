package searchengine.services.serviceIndex;

import searchengine.dto.indexing.IndexResponse;
import searchengine.dto.indexing.IndexResponseError;
import searchengine.exceptions.ExceptionTest;

import java.io.IOException;

public interface IndexService {
    public IndexResponse startIndexing();

    public IndexResponseError stopIndexing() throws InterruptedException;

 //   public IndexResponseError throwsException() throws IOException;
}
