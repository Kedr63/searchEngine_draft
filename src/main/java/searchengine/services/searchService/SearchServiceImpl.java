package searchengine.services.searchService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import searchengine.dto.searching.SearchQuery;
import searchengine.dto.searching.SearchingResponse;
import searchengine.exceptions.EmptyQuerySearchingException;
import searchengine.exceptions.NoSuchLemmaForSearchingInContentException;
import searchengine.exceptions.NoSuchSiteException;
import searchengine.model.*;
import searchengine.repositories.IndexEntityLemmaToPageRepository;
import searchengine.services.indexEntityService.IndexEntityService;
import searchengine.services.lemmaService.LemmaService;
import searchengine.services.pageService.PageService;
import searchengine.services.indexService.LemmaParser;
import searchengine.services.PoolService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    private final PoolService poolService;
    private final IndexEntityLemmaToPageRepository indexEntityLemmaToPageRepository;

    @Value("${errorSearchNotIndexedSite}")
    private String errorSearchNotIndexedSite;
    @Value("${errorSearchQueryEmpty}")
    private String errorSearchQueryEmpty;
    @Value("${errorSearchLemmaInContent}")
    private String errorSearchLemmaInContent;

    @Value("${offset}")
    private int offsetResultSearching;
    @Value(("${limit}"))
    private int limitResultSearching;

    @Value("${percentageAllowedNumberOfPages}")
    private static double percentageAllowedNumberOfPages;

    public SearchServiceImpl(PoolService poolService, IndexEntityLemmaToPageRepository indexEntityLemmaToPageRepository) {
        this.poolService = poolService;
        this.indexEntityLemmaToPageRepository = indexEntityLemmaToPageRepository;
    }

    @Override
    public SearchingResponse search(SearchQuery query) {
        String textQuery = query.getQuery();
        query.setOffset(offsetResultSearching);
        query.setLimit(limitResultSearching);

        Set<LemmaEntity> lemmaEntitySetForSearch;

        if (textQuery.isEmpty()) {
            throw new EmptyQuerySearchingException(errorSearchQueryEmpty);
        }

        List<SiteEntity> siteEntityListForSearching = getSitesToSearchForLemmas(poolService, query);

        try {
            lemmaEntitySetForSearch = getLemmaEntitiesFromQuery(textQuery);
            if (lemmaEntitySetForSearch.isEmpty()) {
                throw new NoSuchLemmaForSearchingInContentException(errorSearchLemmaInContent);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //TODO определить суммарное количество страниц сайта, и если лемма находится более чем на 70% страниц от общей суммы,
        // то удалить лемму из поиска. Готово ниже, протестировать

        removeLemmasThatOftenFoundOnLargeNumberOfPages(lemmaEntitySetForSearch, siteEntityListForSearching, poolService);

        Map<Integer, Set<LemmaEntity>> siteId2LemmaEntitySetSorted = new HashMap<>();
        for (LemmaEntity lemmaEntity : lemmaEntitySetForSearch) {
            //        siteId2LemmaEntitySetSorted.put(lemmaEntity.getSite(), );
        }
        //   for(Map.Entry<Integer, Set<LemmaEntity>> entry : siteId2LemmaEntitySetSorted.entrySet());

        Set<LemmaEntity> lemmaEntitiesSetSorted = lemmaEntitySetForSearch.stream()
                .sorted(Comparator.comparing(LemmaEntity::getFrequency))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<PageEntity> pageEntityListForSearching = new ArrayList<>();
        IndexEntityService indexEntityService = poolService.getIndexEntityService();

        // List<IndexEntity> indexEntityList = new ArrayList<>();

        List<IndexEntity> indexEntityList = getIndexEntityListFromPageWhereLemmaEntities(lemmaEntitiesSetSorted, indexEntityService);

//        for (LemmaEntity lemmaEntity : lemmaEntitiesSetSorted) {
//            indexEntityList = indexEntityService.getIndexEntityListByLemmaId(lemmaEntity.getId());
//
//        }

        return null;
    }

    private List<IndexEntity> getIndexEntityListFromPageWhereLemmaEntities(Set<LemmaEntity> lemmaEntitiesSetSorted, IndexEntityService indexEntityService) {
        List<IndexEntity> indexEntityListResult = new ArrayList<>();
        int setSize = lemmaEntitiesSetSorted.size();
        for (int i = 0; i < setSize; i++) {
            LemmaEntity lemmaEntity = lemmaEntitiesSetSorted.stream().toList().get(i);
            if (i == 0) {
                indexEntityListResult = indexEntityService.getIndexEntityListByLemmaId(lemmaEntity.getId());
            }
            if (i != 0) {
                List<IndexEntity> tempList = new ArrayList<>(indexEntityListResult);
                indexEntityListResult.clear();
                for (IndexEntity indexEntity : indexEntityService.getIndexEntityListByLemmaId(lemmaEntity.getId())) {
                    for (IndexEntity tempIndexEntity : tempList) {
                        int siteId = indexEntity.getLemma().getSite().getId();
                        int pageId = indexEntity.getPageEntity().getId();

                    }
                }


            }
        }
        return indexEntityListResult;
    }


    private List<SiteEntity> getSitesToSearchForLemmas(PoolService poolService, SearchQuery query) {
        List<SiteEntity> siteEntityListForSearching = new ArrayList<>();

        List<SiteEntity> siteEntityIndexedList = poolService.getSiteService().getAllSiteEntities()
                .stream()
                .filter(siteEntity -> siteEntity.getStatus().equals(StatusIndex.INDEXED)).toList();
        if (siteEntityIndexedList.isEmpty()) {
            throw new NoSuchSiteException(errorSearchNotIndexedSite);
        }
        if (query.getSite() != null) {
            for (SiteEntity siteEntity : siteEntityIndexedList) {
                if (siteEntity.getUrl().compareTo(query.getSite()) == 0) {
                    siteEntityListForSearching.add(siteEntity);
                    break;
                }
            }
        } else
            siteEntityListForSearching = siteEntityIndexedList.stream().toList();

        return siteEntityListForSearching;
    }

    private Set<LemmaEntity> getLemmaEntitiesFromQuery(String textQuery) throws IOException {
        Set<LemmaEntity> lemmaEntitySet = new HashSet<>();
        Map<String, Integer> map = new HashMap<>();
        LemmaParser lemmaParser = new LemmaParser(poolService);
        lemmaParser.extractLemmasFromPageTextForMap(textQuery, map);
        Set<String> setLemmaWord = map.keySet();
        LemmaService lemmaService = poolService.getLemmaService();
        for (String lemma : setLemmaWord) {
            Optional<Set<LemmaEntity>> lemmaEntitySetForAdd = lemmaService.getSetLemmaEntityByLemmaWordForm(lemma);

            lemmaEntitySetForAdd.ifPresent(lemmaEntitySet::addAll);
        }
        return lemmaEntitySet;
    }

    private void removeLemmasThatOftenFoundOnLargeNumberOfPages(Set<LemmaEntity> lemmaEntitySet, List<SiteEntity> siteEntityListForSearching, PoolService poolService) {
        PageService pageService = poolService.getPageService();
        for (SiteEntity siteEntity : siteEntityListForSearching) {
            int countPagesOfSite = pageService.getCountPagesOfSite(siteEntity.getId());
            lemmaEntitySet.removeIf(lemmaEntity -> lemmaEntity.getSite().getId() == siteEntity.getId()
                    && lemmaEntity.getFrequency() > countPagesOfSite * percentageAllowedNumberOfPages);

        }
    }


    //   @Override
//    public SearchingResponse search(SearchQuery query) {
//       // String text;
//        List<SiteEntity> siteEntityIndexedList = poolService.getSiteService().getAllSiteEntities()
//                .stream()
//                .filter(siteEntity -> siteEntity.getStatus().equals(StatusIndex.INDEXED)).toList();
//        Map<String, Integer> queryMap = getStringIntegerMap(query, siteEntityIndexedList);
//
//        for(Map.Entry<String, Integer> entry : queryMap.entrySet()) {
//            System.out.println(entry.getKey() + ": " + entry.getValue());
//        }
//
//        List<SearchingResult> searchingResultList = new ArrayList<>();
//        SearchingResult searchingResult = new SearchingResult("ds", "gfd", "dd", "ss", "ll", 0.8f);
//        searchingResultList.add(searchingResult);
//
//        return new SearchingResponse(true, 5, searchingResultList);
//    }

//    private Map<String, Integer> getStringIntegerMap(SearchQuery query, List<SiteEntity> siteEntityList) {
//        if (siteEntityList.isEmpty()) {
//            throw new NoSuchSiteException(errorSearchNotIndexedSite);
//        }
//        if (query.getQuery().isEmpty()) {
//            throw new EmptyQuerySearchingException(errorSearchQueryEmpty);
//        }
//        String text = query.getQuery();
//
//        Map<String, Integer> queryMap = new LinkedHashMap<>();
//        LemmaParser lemmaParser = new LemmaParser(poolService);
//
//        try {
//            lemmaParser.extractLemmaFromTextToMap(text, queryMap);
//        } catch (IOException e) {
//            throw new EmptyQuerySearchingException(e.getMessage());
//        }
//        return queryMap;
//    }
}
