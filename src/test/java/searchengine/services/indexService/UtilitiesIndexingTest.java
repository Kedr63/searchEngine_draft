package searchengine.services.indexService;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.dto.indexing.IndexingResponseError;

import java.util.List;

class UtilitiesIndexingTest {

    List<IndexingResponse> indexingResponseList;

    @BeforeEach
    void setUp() {
        indexingResponseList = List.of(new IndexingResponse(true), new IndexingResponseError(false, "error indexing"));
    }

    @Test
    void getTotalResultIndexingResponse() {
        IndexingResponse expextedResponse = new IndexingResponseError(false, "error indexing");
       IndexingResponse indexingResponseResult = UtilitiesIndexing.getTotalResultIndexingResponse(indexingResponseList);
        Assert.assertEquals(expextedResponse, indexingResponseResult);
    }
}