package searchengine.services.searchService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import searchengine.dto.searching.SearchResult;
import searchengine.model.SiteEntity;
import searchengine.model.StatusIndex;
import searchengine.services.indexService.PoolService;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService{
    private final PoolService poolService;

    @Value("${errorSearchNotIndexedSite}")
    private  String errorSearchNotIndexedSite;
    @Value("${errorSearchQueryEmpty}")
    private String errorSearchQueryEmpty;

    public SearchServiceImpl(PoolService poolService) {
        this.poolService = poolService;
    }


    @Override
    public ResponseEntity<SearchResult> search(String query) {
        List<SiteEntity> siteEntityList = poolService.getSiteService().getAllSiteEntities()
                 .stream()
                 .filter(siteEntity -> siteEntity.getStatus().equals(StatusIndex.INDEXED)).toList();
        if (siteEntityList.isEmpty() && query.isEmpty()) {
         //   return new ResponseEntity<>(new IndexResponseError(false, errorSearchNotIndexedSite), HttpStatus.NOT_FOUND);
        }


        return new ResponseEntity<>(HttpStatus.GONE);
    }
}
