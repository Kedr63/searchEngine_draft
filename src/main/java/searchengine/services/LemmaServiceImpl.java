package searchengine.services;

import org.apache.lucene.morphology.LuceneMorphology;
import org.springframework.stereotype.Service;
import searchengine.model.LemmaEntity;
import searchengine.repositories.LemmaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class LemmaServiceImpl implements LemmaService {

    private final LuceneMorphology luceneMorphology;  //

    private final LemmaRepository lemmaRepository;

  //  protected static final Object lockLemmaRepository = new Object();


    public LemmaServiceImpl(LuceneMorphology luceneMorphology, LemmaRepository lemmaRepository) {
        this.luceneMorphology = luceneMorphology;
        this.lemmaRepository = lemmaRepository;
    }

    @Override
    public List<LemmaEntity> getAllLemmaEntities() {
        return List.of();
    }
    @Transactional
    @Override
    public LemmaEntity getLemmaEntity(int id) {
      //  synchronized (UtilitiesIndexing.lockLemmaRepository) {
          if (lemmaRepository.findById(id).isPresent()) {
              return lemmaRepository.findById(id).get();
            }
          else throw new RuntimeException("Lemma not found");

    }

    @Transactional
    @Override
    public void saveLemmaEntity(LemmaEntity lemmaEntity) {
        //synchronized (UtilitiesIndexing.lockLemmaRepository) {
            lemmaRepository.save(lemmaEntity);

    }

    @Transactional
    @Override
    public boolean isPresentLemmaEntity(String lemma, int siteId) {
     //   synchronized (UtilitiesIndexing.lockLemmaRepository) {
            Optional<String> optionalLemma = lemmaRepository.findByLemma(lemma, siteId);
            return optionalLemma.isPresent();

    }

    @Transactional
    @Override
    public int getLemmaId(String lemma, int siteId) {
       // synchronized (UtilitiesIndexing.lockLemmaRepository) {
            Optional<Integer> optionalId = lemmaRepository.findIdByLemma(lemma, siteId);
            return optionalId.orElse(0);

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

    @Override
    @Transactional
    public void deleteAllLemmaEntities() {
        Logger.getLogger(LemmaServiceImpl.class.getName()).info("Deleting all lemma entities   lemmaRepository.deleteAllLemmaEntity()");
        lemmaRepository.deleteAllLemmaEntity();
    }

    @Override
    @Transactional
    public void deleteLemmaEntityById(int lemmaId) {
        lemmaRepository.deleteById(lemmaId);
    }

    @Override
    @Transactional
    public void updateLemmaFrequency(List<Integer> idLemmaList) {
        for (Integer id : idLemmaList) {
            lemmaRepository.updateLemmaFrequency(id);

        }
    }

    @Override
    @Transactional
    public int getCountLemmasOfSite(int idSiteEntity) {
        return lemmaRepository.getCountLemmasWhereSiteId(idSiteEntity);
    }
}

