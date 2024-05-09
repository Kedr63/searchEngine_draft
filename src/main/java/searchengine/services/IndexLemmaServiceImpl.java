package searchengine.services;

import org.springframework.stereotype.Service;
import searchengine.model.IndexEntity;
import searchengine.repositories.IndexLemmaRepository;

import javax.transaction.Transactional;

@Service
public class IndexLemmaServiceImpl implements IndexLemmaService {

    IndexLemmaRepository indexLemmaRepository;

    protected static  final Object lockIndexLemmaService = new Object();

    public IndexLemmaServiceImpl(IndexLemmaRepository indexLemmaRepository) {
        this.indexLemmaRepository = indexLemmaRepository;
    }


    @Override
    @Transactional
    public IndexEntity getIndexEntityByLemmaId(int lemmaId) {
        synchronized (lockIndexLemmaService) {
            return indexLemmaRepository.findByLemmaId(lemmaId);
        }
    }

    @Override
    public IndexEntity getIndexEntityById(int id) {
        return null;
    }

    @Override
    @Transactional
    public void saveIndexEntity(IndexEntity indexEntity) {
        synchronized (lockIndexLemmaService) {
            indexLemmaRepository.save(indexEntity);
        }
    }
}
