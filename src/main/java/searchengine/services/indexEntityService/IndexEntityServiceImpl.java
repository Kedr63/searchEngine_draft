package searchengine.services.indexEntityService;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import searchengine.dto.dtoToBD.IndexDto;
import searchengine.dto.searching.PageIdInteger;
import searchengine.model.IndexEntity;
import searchengine.repositories.IndexEntityLemmaToPageRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndexEntityServiceImpl implements IndexEntityService {

    private final IndexEntityLemmaToPageRepository indexEntityLemmaToPageRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public IndexEntity getIndexEntityByLemmaId(int lemmaId) {
        return indexEntityLemmaToPageRepository.findByLemmaId(lemmaId);
    }

    @Override
    @Transactional
    public Optional<IndexEntity> getIndexEntityById(int id) {
      return indexEntityLemmaToPageRepository.findById(id);
    }

    @Override
    @Transactional
    public IndexDto saveIndexDto(IndexDto indexDto) {
        IndexEntity savedIndexEntity = indexEntityLemmaToPageRepository.save(modelMapper.map(indexDto, IndexEntity.class));
        return modelMapper.map(savedIndexEntity, IndexDto.class);
    }

    @Override
    @Transactional
    public void deleteAllIndexEntity() {
        Logger.getLogger(IndexEntityLemmaToPageRepository.class.getName()).info("IndexLemmaServiceImpl: deleteAllIndexEntity");
        indexEntityLemmaToPageRepository.deleteAllIndexEntity();
    }

    @Override
    @Transactional
    public void deleteIndexEntityWherePageId(int pageId) {
        indexEntityLemmaToPageRepository.deleteIndexEntityWherePageId(pageId);
    }


    @Override
    @Transactional
    public List<Integer> getIdLemmaByPageId(int idPageEntity) {
        return indexEntityLemmaToPageRepository.findIdLemmaByIdPage(idPageEntity);
    }

    @Override
    @Transactional
    public Set<IndexDto> getSetIndexDtoByLemmaId(int idLemma) {
        return indexEntityLemmaToPageRepository.findIndexEntityBy(idLemma).stream()
                .map(indexEntity -> modelMapper.map(indexEntity, IndexDto.class))
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public Set<PageIdInteger> getPageIdSetByLemmaId(int lemmaId) {
        Set<Integer> integerSetPageId = indexEntityLemmaToPageRepository.findPageIdByLemmaId(lemmaId);
        return integerSetPageId.stream().map(PageIdInteger::new).collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public float getRankByLemmaIdAndPageId(int lemmaId, int pageId) {
        return indexEntityLemmaToPageRepository.findRankByLemmaIdAndPageId(lemmaId, pageId);

    }
}
