package searchengine.services.searchService;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import searchengine.dto.dtoToBD.LemmaDto;
import searchengine.dto.dtoToBD.PageDto;
import searchengine.dto.dtoToBD.SiteDto;
import searchengine.dto.searching.PageIdInteger;
import searchengine.dto.searching.RelativeRelevanceFloater;
import searchengine.dto.searching.SearchingResult;
import searchengine.services.PoolService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


@RequiredArgsConstructor
public class SearchingResultParser {

    private final Set<LemmaDto> lemmaDtoSet;
    private final Map<PageIdInteger, RelativeRelevanceFloater> pageIdToRelativeRelevanceMap;
    private final PoolService poolService;

    public List<SearchingResult> getSearchResultList(int limitResultSearching) {
        List<SearchingResult> searchResultList = new ArrayList<>();
        int counterLimitResultTemp = 0;
        for (Map.Entry<PageIdInteger, RelativeRelevanceFloater> entry : pageIdToRelativeRelevanceMap.entrySet()) {
            PageIdInteger pageIdInteger = entry.getKey();
            RelativeRelevanceFloater relativeRelevanceFloater = entry.getValue();
            PageDto pageDto = poolService.getPageService().getPageDtoById(pageIdInteger.getPageId());

            SearchingResult searchingResult = getSearchingResult(pageDto, relativeRelevanceFloater);
            searchResultList.add(searchingResult);

            counterLimitResultTemp = counterLimitResultTemp + 1;
            if (counterLimitResultTemp == limitResultSearching) {
                break;
            }
        }
        return searchResultList;
    }

    private SearchingResult getSearchingResult(PageDto pageDto, RelativeRelevanceFloater relativeRelevanceFloater) throws IOException {
        SearchingResult searchingResult = new SearchingResult();
        searchingResult.setUri(pageDto.getPath());
        Document document = Jsoup.parse(pageDto.getContent());
        String title = document.select("title").text();
        searchingResult.setTitle(title);
        SiteDto siteDto = new SiteDto();
      //  if (poolService.getSiteService().getSiteDto(pageDto.getSiteId()).isPresent())
        siteDto = poolService.getSiteService().getSiteDto(pageDto.getSiteId()).orElse(siteDto);
        searchingResult.setSite(siteDto.getUrl());
        searchingResult.setSiteName(siteDto.getName());
        searchingResult.setRelevance(relativeRelevanceFloater.getRankValue());

       // SnippetSearcher snippetSearcher = new SnippetSearcherImp(); // применю реализацию SnippetSearcherImp(), и еще можно создать другие реализации
        SnippetSearcher snippetSearcher = new SnippetSearcherRegexImpl(poolService); // применю реализацию SnippetSearcherImp(), и еще можно создать другие реализации
        String snippetResult = snippetSearcher.searchSnippets(document, lemmaDtoSet);
      //  String snippetResult = getSnippets(document, lemmaDtoSet, snippetSearcher);
        searchingResult.setSnippet(snippetResult);
        return searchingResult;
    }

    private String getSnippets(Document document, Set<LemmaDto> lemmaDtoSet, SnippetSearcher snippetSearcher) {
        try {
            return snippetSearcher.searchSnippets(document, lemmaDtoSet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
