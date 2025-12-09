package searchengine.services.searchService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import searchengine.dto.dtoToBD.LemmaDto;
import searchengine.dto.dtoToBD.SiteDto;
import searchengine.dto.searching.*;
import searchengine.exceptions.EmptyQuerySearchingException;
import searchengine.exceptions.NoPagesForSearchingException;
import searchengine.exceptions.NoSuchLemmaForSearchingInContentException;
import searchengine.exceptions.NoSuchSiteException;
import searchengine.model.StatusIndex;
import searchengine.services.PoolService;
import searchengine.services.indexEntityService.IndexEntityService;
import searchengine.services.indexService.lemmaParser.LemmaParseable;
import searchengine.services.indexService.lemmaParser.LemmaParser;
import searchengine.services.lemmaService.LemmaService;
import searchengine.services.pageService.PageService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SearchServiceImpl implements SearchService {

    private final PoolService poolService;

    @Value("${errorSearchNotIndexedSite}")
    private String errorSearchNotIndexedSite;
    @Value("${errorSearchQueryEmpty}")
    private String errorSearchQueryEmpty;
    @Value("${errorSearchLemmaInContent}")
    private String errorSearchLemmaInContent;
    @Value("${errorNoPagesWithSuchSearchQuery}")
    private String errorNoPagesWithSuchSearchQuery;

    @Value("${offset}")
    private int offsetResultSearching;
    @Value("${limit}")
    private int limitResultSearching;

    @Value("${percentageAllowedNumberOfPages}")
    private double percentageAllowedNumberOfPages;

    public SearchServiceImpl(PoolService poolService) {
        this.poolService = poolService;
    }

    @Override
    public SearchingResponse search(SearchQuery searchQuery) {
        String textQuery = searchQuery.getQuery();
        searchQuery.setOffset(offsetResultSearching);
        searchQuery.setLimit(limitResultSearching);

        if (textQuery.isEmpty()) {
            throw new EmptyQuerySearchingException(errorSearchQueryEmpty);
        }

        List<SiteDto> siteDtoListForSearching = getSitesWhereSearchingForLemmas(searchQuery);

        Set<LemmaDto> lemmaDtoSetFromSearchQuery;
        try {
            lemmaDtoSetFromSearchQuery = getLemmaDtoSetFromQuery(textQuery, siteDtoListForSearching);
            if (lemmaDtoSetFromSearchQuery.isEmpty()) {
                throw new NoSuchLemmaForSearchingInContentException(errorSearchLemmaInContent);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Set<LemmaDto> lemmaDtoSetFilteredWithoutOftenOccur = removeLemmasThatOfOccurOnTooManyPages(lemmaDtoSetFromSearchQuery, siteDtoListForSearching);
        if (lemmaDtoSetFilteredWithoutOftenOccur.isEmpty()) {
            throw new NoSuchLemmaForSearchingInContentException(errorSearchLemmaInContent);
        }

        Set<LemmaDto> lemmaDtoSetSortedByAscendingFrequency = lemmaDtoSetFilteredWithoutOftenOccur.stream()
                .sorted(Comparator.comparing(LemmaDto::getFrequency))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<PageIdInteger> pageIdSetForSearchingWhereAllLemmasFind = getPageSetWhereLemmasFind(lemmaDtoSetSortedByAscendingFrequency);
        if (pageIdSetForSearchingWhereAllLemmasFind.isEmpty()) {
            throw new NoPagesForSearchingException(errorNoPagesWithSuchSearchQuery);
        }
        // TODO здесь продолжить проверять
        RelevanceCalculator relevanceCalculator = new RelevanceCalculator(lemmaDtoSetSortedByAscendingFrequency,
                pageIdSetForSearchingWhereAllLemmasFind,
                poolService);
        Map<PageIdInteger, RelativeRelevanceFloater> pageToRelativeRelevanceSortedByDescRelevanceMap =
                relevanceCalculator.getCalculatedPageRelevance();

        return getSearchingResponse(lemmaDtoSetSortedByAscendingFrequency, pageToRelativeRelevanceSortedByDescRelevanceMap);
    }


    private SearchingResponse getSearchingResponse(Set<LemmaDto> lemmaDtoSortedAscFrequencySet, Map<PageIdInteger, RelativeRelevanceFloater> pageIdToRelativeRelevanceSortedMap) {
        SearchingResultParser searchingResultParser = new SearchingResultParser(lemmaDtoSortedAscFrequencySet, pageIdToRelativeRelevanceSortedMap, poolService);
        List<SearchingResult> searchResultList = searchingResultParser.getSearchResultList(limitResultSearching);

        SearchingResponse searchingResponse = new SearchingResponse();
        searchingResponse.setResult(true);
        searchingResponse.setCount(searchResultList.size());
        searchingResponse.setData(searchResultList);
        return searchingResponse;
    }

    /**
     * Метод получит список Id страниц где леммы найдутся следующим образом: По первой, самой редкой лемме
     * из списка, находить все страницы, на которых она встречается. Далее искать соответствия следующей леммы
     * из этого списка страниц, а затем повторять операцию по каждой следующей лемме. Список страниц при этом
     * на каждой итерации должен уменьшаться.
     * <p></p>
     *
     * @return Список id страниц, на которых есть все леммы из сортированного набора {@code Set<LemmaDto> setLemmaDtoSorted}.
     * Или если в итоге не осталось ни одной страницы, то выведет пустой список.
     */
    private Set<PageIdInteger> getPageSetWhereLemmasFind(Set<LemmaDto> lemmaDtoSetSorted) {
        Set<PageIdInteger> pageIdIntegerSetForSearching = new HashSet<>();
        IndexEntityService indexEntityService = poolService.getIndexEntityService();
        for (int i = 0; i < lemmaDtoSetSorted.size(); i++) {
            LemmaDto lemmaDto = lemmaDtoSetSorted.stream().toList().get(i);
            if (i == 0) { // так получим PageSet (он основа) по самой редкой лемме
                pageIdIntegerSetForSearching = indexEntityService.getPageIdSetByLemmaId(lemmaDto.getId());
            }
            if (i != 0) {
                Set<PageIdInteger> pageIdSetForRetain = indexEntityService.getPageIdSetByLemmaId(lemmaDto.getId());
                pageIdIntegerSetForSearching.retainAll(pageIdSetForRetain); // .retain оставляет (удержит) в setPageIdForSearching все (Э),
                // которые есть в setPageIdTemp
            }
        }
        return pageIdIntegerSetForSearching; // список страниц на которых есть все леммы указанные в setLemmaDtoSorted
    }

    /**
     * Получим список сайтов в которых будем искать леммы
     * (если сайт не определен в форме браузера, тогда ищем сайты со статусом "INDEXED")
     *
     * @param searchQuery данные из поисковой формы браузера
     *                    {@code http://localhost:8080/api/search?query=купить прицеп&offset=0&limit=7}
     */
    private List<SiteDto> getSitesWhereSearchingForLemmas(SearchQuery searchQuery) {
        List<SiteDto> ListSiteDtoForSearching = new ArrayList<>();

        List<SiteDto> ListSiteDtoThatHasIndexedStatus = poolService.getSiteService().getAllSiteDto()
                .stream()
                .filter(siteDto -> siteDto.getStatusIndex().equals(StatusIndex.INDEXED)).toList();
        if (ListSiteDtoThatHasIndexedStatus.isEmpty()) {
            throw new NoSuchSiteException(errorSearchNotIndexedSite);
        }
        if (searchQuery.getSite() != null) {  // если в форме задан конкретный сайт для поиска
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

    private Set<LemmaDto> getLemmaDtoSetFromQuery(String textQuery, List<SiteDto> siteDtoListForSearching) throws IOException {
        Set<LemmaDto> lemmaDtoSet = new HashSet<>();
        LemmaParseable lemmaParser = new LemmaParser(poolService);
        /* универсальный метод: применим его, поэтому пришлось создать map */
        Map<String, Integer> tempMap = lemmaParser.extractLemmaToAmountFromTextForMap(textQuery);
        Set<String> setLemmaWordFromMap = tempMap.keySet();
        LemmaService lemmaService = poolService.getLemmaService();
        for (SiteDto siteDto : siteDtoListForSearching) {
            for (String lemma : setLemmaWordFromMap) {
                LemmaDto LemmaDtoForAdd = lemmaService.getLemmaDtoByLemmaWordFormAndSiteId(lemma, siteDto.getId());
                lemmaDtoSet.add(LemmaDtoForAdd);
            }
        }
        return lemmaDtoSet;
    }

    /**
     * Если лемма находится, например, более чем на 70% страниц
     * от общей суммы,то удалить лемму из поиска. Подобрать %. Установку % делаем в конфигурацонном файле {@code application.yaml}
     */
    private Set<LemmaDto> removeLemmasThatOfOccurOnTooManyPages(Set<LemmaDto> setLemmaDtoFromQuery,
                                                                List<SiteDto> siteDtoListForSearching) {
        PageService pageService = poolService.getPageService();
        for (SiteDto siteDto : siteDtoListForSearching) {
            int countPagesOfSite = pageService.getCountPagesOfSite(siteDto.getId());
            setLemmaDtoFromQuery.removeIf(lemmaDto -> lemmaDto.getSiteId() == siteDto.getId()
                    && lemmaDto.getFrequency() > countPagesOfSite * percentageAllowedNumberOfPages);
        }
        return setLemmaDtoFromQuery;
    }
}
