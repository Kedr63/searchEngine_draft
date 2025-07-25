package searchengine.services.indexEntityService;


import searchengine.dto.dtoToBD.IndexDto;
import searchengine.model.IndexEntity;
import searchengine.dto.searching.PageIdInteger;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IndexEntityService {

    IndexEntity getIndexEntityByLemmaId(int lemmaId);

    Optional <IndexEntity> getIndexEntityById(int id);

    IndexDto saveIndexDto(IndexDto indexDto);

    void deleteAllIndexEntity();

    void deleteIndexEntityWherePageId(int PageId);

    List<Integer> getIdLemmaByPageId(int idPageEntity);

    Set<IndexDto> getSetIndexDtoByLemmaId(int lemmaId);

    Set<PageIdInteger> getPageIdSetByLemmaId(int lemmaId);

    float getRankByLemmaIdAndPageId(int lemmaId, int pageId);
}
