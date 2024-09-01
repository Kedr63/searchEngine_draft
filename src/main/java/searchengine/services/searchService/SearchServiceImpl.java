package searchengine.services.searchService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import searchengine.dto.searching.SearchQuery;
import searchengine.dto.searching.SearchingResponse;
import searchengine.dto.searching.SearchingResult;
import searchengine.exceptions.EmptyQuerySearchingException;
import searchengine.exceptions.NoSuchSiteException;
import searchengine.model.SiteEntity;
import searchengine.model.StatusIndex;
import searchengine.repositories.IndexEntityLemmaToPageRepository;
import searchengine.services.indexService.LemmaParser;
import searchengine.services.indexService.PoolService;

import java.io.IOException;
import java.util.*;

@Service
public class SearchServiceImpl implements SearchService{

    private final PoolService poolService;
    private final IndexEntityLemmaToPageRepository indexEntityLemmaToPageRepository;

    @Value("${errorSearchNotIndexedSite}")
    private  String errorSearchNotIndexedSite;
    @Value("${errorSearchQueryEmpty}")
    private String errorSearchQueryEmpty;

    public SearchServiceImpl(PoolService poolService, IndexEntityLemmaToPageRepository indexEntityLemmaToPageRepository) {
        this.poolService = poolService;
        this.indexEntityLemmaToPageRepository = indexEntityLemmaToPageRepository;
    }


    @Override
    public SearchingResponse search(SearchQuery query) {
       // String text;
        List<SiteEntity> siteEntityList = poolService.getSiteService().getAllSiteEntities()
                .stream()
                .filter(siteEntity -> siteEntity.getStatus().equals(StatusIndex.INDEXED)).toList();
        Map<String, Integer> queryMap = getStringIntegerMap(query, siteEntityList);

        for(Map.Entry<String, Integer> entry : queryMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        List<SearchingResult> searchingResultList = new ArrayList<>();
        SearchingResult searchingResult = new SearchingResult("ds", "gfd", "dd", "ss", "ll", 0.8f);
        searchingResultList.add(searchingResult);

        return new SearchingResponse(true, 5, searchingResultList);
    }

    private Map<String, Integer> getStringIntegerMap(SearchQuery query, List<SiteEntity> siteEntityList) {
        if (siteEntityList.isEmpty()) {
            throw new NoSuchSiteException(errorSearchNotIndexedSite);
        }
        if (query.getQuery().isEmpty()) {
            throw new EmptyQuerySearchingException(errorSearchQueryEmpty);
        }
        String text = query.getQuery();

        Map<String, Integer> queryMap = new LinkedHashMap<>();
        LemmaParser lemmaParser = new LemmaParser(poolService);

        try {
            lemmaParser.extractLemmaFromTextToMap(text, queryMap);
        } catch (IOException e) {
            throw new EmptyQuerySearchingException(e.getMessage());
        }
        return queryMap;
    }
}
