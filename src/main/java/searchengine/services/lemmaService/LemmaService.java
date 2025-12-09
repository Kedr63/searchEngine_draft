package searchengine.services.lemmaService;

import searchengine.dto.dtoToBD.LemmaDto;
import searchengine.model.LemmaEntity;

import java.util.List;
import java.util.Optional;

public interface LemmaService {


    List<LemmaEntity> getAllLemmaEntities();

    LemmaDto getLemmaDtoById(int id);

    LemmaDto getLemmaDtoByLemmaWordFormAndSiteId(String lemmaWord, int siteId);

    LemmaDto saveLemmaDto(LemmaDto lemmaDto);

    boolean isPresentLemmaEntity(String lemma, int siteId);

    Optional<Integer> getLemmaId(String lemma, int siteId);

    List<String> getMorphologyFormsInfo(String word);

    String getNormalBaseFormWord(String word);

    boolean hasWordInDictionary(String word);

    void deleteAllLemmaEntities();

    void deleteLemmaEntityById(int lemmaId);

    void updateLemmaFrequency(List<Integer> idLemmaList);

    int getCountLemmasOfSite(int idSiteEntity);
}
