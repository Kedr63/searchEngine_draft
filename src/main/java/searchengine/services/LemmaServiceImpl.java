package searchengine.services;

import org.apache.lucene.morphology.LuceneMorphology;
import org.springframework.stereotype.Service;
import searchengine.model.LemmaEntity;
import searchengine.repositories.LemmaRepository;
import searchengine.services.indexService.UtilitiesIndexing;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class LemmaServiceImpl implements LemmaService {

    private final LuceneMorphology luceneMorphology;  //

    private final LemmaRepository lemmasRepository;

  //  protected static final Object lockLemmaRepository = new Object();


    public LemmaServiceImpl(LuceneMorphology luceneMorphology, LemmaRepository lemmaRepository) {
        this.luceneMorphology = luceneMorphology;
        this.lemmasRepository = lemmaRepository;
    }

    @Override
    public List<LemmaEntity> getAllLemmaEntities() {
        return List.of();
    }
    @Transactional
    @Override
    public LemmaEntity getLemmaEntity(int id) {
        synchronized (UtilitiesIndexing.lockLemmaRepository) {
            return lemmasRepository.findById(id).orElse(null);
        }
    }

    @Transactional
    @Override
    public void saveLemmaEntity(LemmaEntity lemmaEntity) {
        synchronized (UtilitiesIndexing.lockLemmaRepository) {
            lemmasRepository.save(lemmaEntity);
        }
    }

    @Transactional
    @Override
    public boolean isPresentLemmaEntity(String lemma, int siteId) {
        synchronized (UtilitiesIndexing.lockLemmaRepository) {
            Optional<String> optionalLemma = lemmasRepository.findByLemma(lemma, siteId);
            return optionalLemma.isPresent();
        }
    }

    @Transactional
    @Override
    public int getLemmaId(String lemma, int siteId) {
        synchronized (UtilitiesIndexing.lockLemmaRepository) {
            Optional<Integer> optionalId = lemmasRepository.findIdByLemma(lemma, siteId);
            return optionalId.orElse(0);
        }
    }

    @Override
    public List<String> getMorphologyForms(String word) {
        return luceneMorphology.getMorphInfo(word);
    }

    @Override
    public String getNormalBaseFormWord(String word) {
        return (luceneMorphology.getNormalForms(word)).get(0);
    }

    @Override
    public boolean isValidWord(String word) {
        return luceneMorphology.checkString(word);
    }
}

