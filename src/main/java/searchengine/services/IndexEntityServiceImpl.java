package searchengine.services;

import org.springframework.stereotype.Service;
import searchengine.model.IndexEntity;
import searchengine.repositories.IndexEntityLemmaToPageRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.logging.Logger;

@Service
public class IndexEntityServiceImpl implements IndexEntityService {

    private final IndexEntityLemmaToPageRepository indexEntityLemmaToPageRepository;


    public IndexEntityServiceImpl(IndexEntityLemmaToPageRepository indexEntityLemmaToPageRepository) {
        this.indexEntityLemmaToPageRepository = indexEntityLemmaToPageRepository;
    }


    @Override
    @Transactional
    public IndexEntity getIndexEntityByLemmaId(int lemmaId) {
       // synchronized (UtilitiesIndexing.lockIndexLemmaService) {
            return indexEntityLemmaToPageRepository.findByLemmaId(lemmaId);

    }

    @Override
    @Transactional
    public IndexEntity getIndexEntityById(int id) {
        return null;
    }

    @Override
    @Transactional
    public void saveIndexEntity(IndexEntity indexEntity) {
       // synchronized (UtilitiesIndexing.lockIndexLemmaService) {
            indexEntityLemmaToPageRepository.save(indexEntity);

    }


    @Override
    @Transactional
    public void deleteAllIndexEntity() {
        Logger.getLogger(IndexEntityLemmaToPageRepository.class.getName()).info("IndexLemmaServiceImpl: deleteAllIndexEntity");
        indexEntityLemmaToPageRepository.deleteAllIndexLemmaEntity();
    }

    @Override
    @Transactional
    public void deleteIndexEntityWherePageId(int pageId) {
//       List<IndexEntity> indexEntityList = indexEntityLemmaToPageRepository.findAll()
//               .stream()
//               .filter(indexEntity->indexEntity.getPage().getId() == pageId).toList();
        indexEntityLemmaToPageRepository.deleteIndexEntityWherePageId(pageId);
//       for (IndexEntity indexEntity : indexEntityList) {
//           int lemmaId = indexEntity.getLemma().getId();
//           lemmaService.deleteLemmaEntityById(lemmaId);
//       }

    }

    @Override
    @Transactional
    public List<Integer> getIdLemmaByPageId(int idPageEntity) {
        return indexEntityLemmaToPageRepository.findIdLemmaByIdPage(idPageEntity);
    }
}
