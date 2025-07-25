package searchengine.services.searchService;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import searchengine.dto.dtoToBD.LemmaDto;
import searchengine.dto.searching.AbsRelevanceFloater;
import searchengine.dto.searching.PageIdInteger;
import searchengine.dto.searching.RelativeRelevanceFloater;
import searchengine.services.PoolService;

import java.util.*;

@Data
@RequiredArgsConstructor
public class RelevanceCalculator {

//    Set<LemmaDto> setLemmaDto;
//    Set<PageIdInteger> setPageId;
    private final PoolService poolService;

//    public RelevanceCalculator() {
//    }

//    public RelevanceCalculator(PoolService poolService) {
//        this.poolService = poolService;
//    }

    protected Map<PageIdInteger, RelativeRelevanceFloater> getCalculatedPageRelevance(Set<PageIdInteger> pageIdForSearchingSet,
                                                                                      Set<LemmaDto> lemmaDtoSetSortedByAscendingFrequency) {
        Map<PageIdInteger, AbsRelevanceFloater> pageIdToAbsRelevanceMap =
                getMappingPageIdToAbsRelevance(pageIdForSearchingSet, lemmaDtoSetSortedByAscendingFrequency);
        return getMappingPageIdToRelativeRelevanceSortedDesc(pageIdToAbsRelevanceMap);
    }

    private Map<PageIdInteger, AbsRelevanceFloater> getMappingPageIdToAbsRelevance(Set<PageIdInteger> pageIdForSearchingSet,
                                                                                   Set<LemmaDto> lemmaDtoSetSortedByAscendingFrequency) {
        Map<PageIdInteger, AbsRelevanceFloater> pageIdToAbsRelevanceMap = new HashMap<>();
        for (PageIdInteger pageIdInteger : pageIdForSearchingSet) {
            float absRelevance = 0;
            for (LemmaDto lemmaDto : lemmaDtoSetSortedByAscendingFrequency) {
                float rankOfLemmaDto = poolService.getIndexEntityService().getRankByLemmaIdAndPageId(lemmaDto.getId(), pageIdInteger.getPageId());
                absRelevance = absRelevance + rankOfLemmaDto;
            }
            pageIdToAbsRelevanceMap.put(pageIdInteger, new AbsRelevanceFloater(absRelevance));
        }
        return pageIdToAbsRelevanceMap;
    }

    private Map<PageIdInteger, RelativeRelevanceFloater> getMappingPageIdToRelativeRelevanceSortedDesc(Map<PageIdInteger, AbsRelevanceFloater> pageIdToAbsRelevanceMap) {
        Map<PageIdInteger, RelativeRelevanceFloater> pageIdToRelativeRelevanceMap = new HashMap<>();
        AbsRelevanceFloater maxValueAbsRelevanceFloater = Collections.max(pageIdToAbsRelevanceMap.values());
        List<RelativeRelevanceFloater> tempListValueOfMap = new ArrayList<>();
        Map<PageIdInteger, RelativeRelevanceFloater> pageIdToRelativeRelevanceSortedDescMap = new LinkedHashMap<>();

        for (Map.Entry<PageIdInteger, AbsRelevanceFloater> entry : pageIdToAbsRelevanceMap.entrySet()) {
            AbsRelevanceFloater absRelevanceFloater = entry.getValue();
            RelativeRelevanceFloater relativeRelevanceFloater =
                    new RelativeRelevanceFloater((absRelevanceFloater.getRankValue()) / (maxValueAbsRelevanceFloater.getRankValue()));
            pageIdToRelativeRelevanceMap.put(entry.getKey(), relativeRelevanceFloater);
            tempListValueOfMap.add(relativeRelevanceFloater);
        }

        Collections.sort(tempListValueOfMap);
        Collections.reverse(tempListValueOfMap); // по этому порядку упорядочить map
        for (RelativeRelevanceFloater relativeRelevanceFloater : tempListValueOfMap) {
            for (Map.Entry<PageIdInteger, RelativeRelevanceFloater> entry : pageIdToRelativeRelevanceMap.entrySet()) {
                if (entry.getValue().equals(relativeRelevanceFloater)) {
                    pageIdToRelativeRelevanceSortedDescMap.put(entry.getKey(), relativeRelevanceFloater);
                }
            }
        }
        return pageIdToRelativeRelevanceSortedDescMap;
    }
}
