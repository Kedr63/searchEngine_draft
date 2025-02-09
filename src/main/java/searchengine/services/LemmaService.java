package searchengine.services;

import searchengine.model.LemmaEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LemmaService {


    List<LemmaEntity> getAllLemmaEntities();

    LemmaEntity getLemmaEntityById(int id);

    Optional<Set<LemmaEntity>> getSetLemmaEntityByLemmaWordForm(String lemmaWord);

    void saveLemmaEntity(LemmaEntity lemmaEntity);

    boolean isPresentLemmaEntity(String lemma, int siteId);

    int getLemmaId(String lemma, int siteId);

    List<String> getMorphologyForms(String word);

    String getNormalBaseFormWord(String word);

    boolean isValidWord(String word);

    void deleteAllLemmaEntities();

    void deleteLemmaEntityById(int lemmaId);

    void updateLemmaFrequency(List<Integer> idLemmaList);

    int getCountLemmasOfSite(int idSiteEntity);
}
