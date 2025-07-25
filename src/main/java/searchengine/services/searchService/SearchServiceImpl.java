package searchengine.services.searchService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import searchengine.dto.dtoToBD.IndexDto;
import searchengine.dto.dtoToBD.LemmaDto;
import searchengine.dto.dtoToBD.SiteDto;
import searchengine.dto.searching.*;
import searchengine.exceptions.EmptyQuerySearchingException;
import searchengine.exceptions.NoSuchLemmaForSearchingInContentException;
import searchengine.exceptions.NoSuchSiteException;
import searchengine.model.StatusIndex;
import searchengine.repositories.IndexEntityLemmaToPageRepository;
import searchengine.services.PoolService;
import searchengine.services.indexEntityService.IndexEntityService;
import searchengine.services.indexService.LemmaParser;
import searchengine.services.lemmaService.LemmaService;
import searchengine.services.pageService.PageService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SearchServiceImpl implements SearchService {

    private final IndexEntityLemmaToPageRepository indexEntityLemmaToPageRepository;
    private final PoolService poolService;

  //  private final SnippetSearcherConfiguration snippetSearcherConfiguration;

    @Value("${errorSearchNotIndexedSite}")
    private String errorSearchNotIndexedSite;
    @Value("${errorSearchQueryEmpty}")
    private String errorSearchQueryEmpty;
    @Value("${errorSearchLemmaInContent}")
    private String errorSearchLemmaInContent;

    @Value("${offset}")
    private int offsetResultSearching;
    @Value("${limit}")
    private int limitResultSearching;

    @Value("${percentageAllowedNumberOfPages}")
    private double percentageAllowedNumberOfPages;

    public SearchServiceImpl(PoolService poolService, IndexEntityLemmaToPageRepository indexEntityLemmaToPageRepository) {
        this.poolService = poolService;
        this.indexEntityLemmaToPageRepository = indexEntityLemmaToPageRepository;
    }

    @Override
    public SearchingResponse search(SearchQuery searchQuery) {
        String textQuery = searchQuery.getQuery();
        searchQuery.setOffset(offsetResultSearching);
        searchQuery.setLimit(limitResultSearching);

        if (textQuery.isEmpty()) {
            throw new EmptyQuerySearchingException(errorSearchQueryEmpty);
        }
        // get list sites where we'll look for lemmas (if site not defined in form browser, then searching in sites with status "INDEXED")
        List<SiteDto> siteDtoListForSearching = getSitesWhereSearchingForLemmas(searchQuery);

        // нужно получить lemmaDto чтоб применить данные по frequency (кол-во стр на которых слово встр-ся) и siteId, id
        Set<LemmaDto> lemmaDtoSetFromSearchQuery;
        try {
            lemmaDtoSetFromSearchQuery = getLemmaDtoFromQuery(textQuery);
            if (lemmaDtoSetFromSearchQuery.isEmpty()) {
                throw new NoSuchLemmaForSearchingInContentException(errorSearchLemmaInContent);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //TODO определить суммарное количество страниц сайта, и если лемма находится, например, более чем на 70% (подобрать %) страниц
        // от общей суммы,то удалить лемму из поиска. Готово ниже, протестировать

        Set<LemmaDto> lemmaDtoSetFilteredFromFrequentOccurrenceOnPages =
                removeLemmasThatOftenFoundOnLargeNumberOfPages(lemmaDtoSetFromSearchQuery, siteDtoListForSearching);
        if (lemmaDtoSetFilteredFromFrequentOccurrenceOnPages.isEmpty()) {
            throw new NoSuchLemmaForSearchingInContentException(errorSearchLemmaInContent);
        }

        Set<LemmaDto> lemmaDtoSetSortedByAscendingFrequency = lemmaDtoSetFilteredFromFrequentOccurrenceOnPages.stream()
                .sorted(Comparator.comparing(LemmaDto::getFrequency))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // TODO найти страницы где леммы встречаются
        Set<PageIdInteger> pageIdSetForSearchingWhereAllLemmasFind = getPageSetWhereLemmasFind(lemmaDtoSetSortedByAscendingFrequency);

        // TODO Ищем релевантности
        RelevanceCalculator relevanceCalculator = new RelevanceCalculator(poolService);
        Map<PageIdInteger, RelativeRelevanceFloater> pageToRelativeRelevanceSortedByDescRelevanceMap =
                relevanceCalculator.getCalculatedPageRelevance(pageIdSetForSearchingWhereAllLemmasFind, lemmaDtoSetSortedByAscendingFrequency);

        // конец заглушки

        // IndexEntityService indexEntityService = poolService.getIndexEntityService();
        // List<IndexEntity> indexEntityList = new ArrayList<>();

        //   Set<IndexDto> setIndexDto = getIndexDtoListFromPageWhereLemmaDto(setLemmaDtoSorted, indexEntityService);

//        for (LemmaEntity lemmaEntity : lemmaEntitiesSetSorted) {
//            indexEntityList = indexEntityService.getIndexEntityListByLemmaId(lemmaEntity.getId());
//
//        }

        return getSearchingResponse(lemmaDtoSetSortedByAscendingFrequency, pageToRelativeRelevanceSortedByDescRelevanceMap);
    }

    private SearchingResponse getSearchingResponse(Set<LemmaDto> lemmaDtoSortedAscFrequencySet, Map<PageIdInteger, RelativeRelevanceFloater> pageIdToRelativeRelevanceSortedMap) {
        SearchingResultParser searchingResultParser
                = new SearchingResultParser(lemmaDtoSortedAscFrequencySet, pageIdToRelativeRelevanceSortedMap, poolService);
        List<SearchingResult> searchResultList = searchingResultParser.getSearchResultList(limitResultSearching);

        SearchingResponse searchingResponse = new SearchingResponse();
        searchingResponse.setResult(true);
        searchingResponse.setCount(searchResultList.size());
        searchingResponse.setData(searchResultList);
        return searchingResponse;
    }


    private Set<PageIdInteger> getPageSetWhereLemmasFind(Set<LemmaDto> setLemmaDtoSorted) {
        Set<PageIdInteger> setPageIdIntegerForSearching = new HashSet<>();
        IndexEntityService indexEntityService = poolService.getIndexEntityService();
        // По первой, самой редкой лемме из списка, находить все страницы, на которых она встречается.
        // Далее искать соответствия следующей леммы из этого списка страниц, а затем повторять операцию по каждой следующей лемме.
        // Список страниц при этом на каждой итерации должен уменьшаться
        for (int i = 0; i < setLemmaDtoSorted.size(); i++) {
            LemmaDto lemmaDto = setLemmaDtoSorted.stream().toList().get(i);
            if (i == 0) { // так получим PageSet (он основа) по самой редкой лемме
                setPageIdIntegerForSearching = indexEntityService.getPageIdSetByLemmaId(lemmaDto.getId());
            }
            if (i != 0) {
                Set<PageIdInteger> setPageIdTemp = indexEntityService.getPageIdSetByLemmaId(lemmaDto.getId());
                setPageIdIntegerForSearching.retainAll(setPageIdTemp); // .retain оставляет в setPageIdForSearching все (Э),
                // которые есть в setPageIdTemp
            }
        }
        return setPageIdIntegerForSearching; // список страниц на которых есть все леммы указанные в setLemmaDtoSorted
    }

    // пока не тот вариант (может быть удалить)
    private Set<IndexDto> getIndexDtoListFromPageWhereLemmaDto(Set<LemmaDto> lemmaDtoSetSorted, IndexEntityService indexEntityService) {
        Set<IndexDto> setIndexDtoResult = new HashSet<>();
        int setSize = lemmaDtoSetSorted.size();
        for (int i = 0; i < setSize; i++) {
            LemmaDto lemmaDto = lemmaDtoSetSorted.stream().toList().get(i);
            if (i == 0) {
                setIndexDtoResult = indexEntityService.getSetIndexDtoByLemmaId(lemmaDto.getId());
            }
            if (i != 0) {
                Set<IndexDto> setIndexDtoNext = indexEntityService.getSetIndexDtoByLemmaId(lemmaDto.getId());
                Set<IndexDto> tempSet = new HashSet<>(setIndexDtoResult);
                tempSet.retainAll(setIndexDtoNext);
                setIndexDtoResult.clear();
                setIndexDtoResult.addAll(tempSet);
//                for (IndexDto indexDto : indexEntityService.getSetIndexDtoByLemmaId(lemmaDto.getId())) {
//                    for (IndexDto tempIndexEntity : tempSet) {
//                     //   int siteId = indexDto.getLemmaId().getSite().getId();
//                       // int pageId = indexDto.getPageEntity().getId();
//
//                    }
//                }
            }
        }
        return setIndexDtoResult;
    }


    private List<SiteDto> getSitesWhereSearchingForLemmas(SearchQuery searchQuery) {
        List<SiteDto> ListSiteDtoForSearching = new ArrayList<>();

        List<SiteDto> ListSiteDtoThatHasIndexedStatus = poolService.getSiteService().getAllSiteDto()
                .stream()
                .filter(siteDto -> siteDto.getStatusIndex().equals(StatusIndex.INDEXED)).toList();
        if (ListSiteDtoThatHasIndexedStatus.isEmpty()) {
            throw new NoSuchSiteException(errorSearchNotIndexedSite);
        }
        if (searchQuery.getSite() != null) {  // если в форме задан сайт для поиска
            for (SiteDto siteDto : ListSiteDtoThatHasIndexedStatus) {
                if (siteDto.getUrl().compareTo(searchQuery.getSite()) == 0) {
                    ListSiteDtoForSearching.add(siteDto);
                    break;
                }
            }
        } else  // если не указан ни один сайт. то ищем по всем индексированным сайтам
            ListSiteDtoForSearching = ListSiteDtoThatHasIndexedStatus.stream().toList();

        return ListSiteDtoForSearching;
    }

    private Set<LemmaDto> getLemmaDtoFromQuery(String textQuery) throws IOException {
        Set<LemmaDto> setLemmaDto = new HashSet<>();
        // Map<String, Integer> tempMap;
        LemmaParser lemmaParser = new LemmaParser(poolService);
        /* универсальный метод: применим его, поэтому пришлось создать map */
        Map<String, Integer> tempMap = lemmaParser.extractFutureLemmasFromTextForMap(textQuery);
        Set<String> setLemmaWordFromMap = tempMap.keySet();
        LemmaService lemmaService = poolService.getLemmaService();
        for (String lemma : setLemmaWordFromMap) {
            Set<LemmaDto> setLemmaDtoForAdd = lemmaService.getSetLemmaDtoByLemmaWordForm(lemma);
            setLemmaDto.addAll(setLemmaDtoForAdd);
        }
        return setLemmaDto;
    }

    private Set<LemmaDto> removeLemmasThatOftenFoundOnLargeNumberOfPages(Set<LemmaDto> setLemmaDtoFromQuery,
                                                                         List<SiteDto> siteDtoListForSearching) {
        PageService pageService = poolService.getPageService();
        for (SiteDto siteDto : siteDtoListForSearching) {
            int countPagesOfSite = pageService.getCountPagesOfSite(siteDto.getId());
            setLemmaDtoFromQuery.removeIf(lemmaDto -> lemmaDto.getSiteId() == siteDto.getId()
                    && lemmaDto.getFrequency() > countPagesOfSite * percentageAllowedNumberOfPages);
        }
        return setLemmaDtoFromQuery;
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
