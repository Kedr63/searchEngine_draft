package searchengine.services;

import searchengine.model.LemmaEntity;

import java.util.List;

public interface LemmaService {

    // Object lockLemmaRepository = new Object();


    List<LemmaEntity> getAllLemmaEntities();

    LemmaEntity getLemmaEntity(int id);

    void saveLemmaEntity(LemmaEntity lemmaEntity);

    boolean isPresentLemmaEntity(String lemma, int siteId);

    int getLemmaId(String lemma, int siteId);

    List<String> getMorphologyForms(String word);

    String getNormalBaseFormWord(String word);

    boolean isValidWord(String word);
}
