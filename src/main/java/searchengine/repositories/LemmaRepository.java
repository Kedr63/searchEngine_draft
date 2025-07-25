package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.LemmaEntity;

import java.util.Optional;
import java.util.Set;

@Repository
public interface LemmaRepository extends JpaRepository<LemmaEntity, Integer> {

    @Query(value = "SELECT lemma FROM search_engine.lemma where lemma LIKE :lemma AND site_id =:siteId", nativeQuery = true)
    Optional<String> findByLemma(String lemma, int siteId);

    @Query(value = "SELECT id FROM search_engine.lemma where lemma LIKE :lemma AND site_id =:siteId", nativeQuery = true)
    Optional<Integer> findIdByLemma(String lemma, int siteId);

    @Modifying // без этой аннотации не сработает запрос на изменение таблицы
    @Query(value = "delete FROM search_engine.lemma", nativeQuery = true)
    void deleteAllLemmaEntity();

    @Modifying
    @Query(value = "update search_engine.lemma set frequency=frequency-1 WHERE id=:id", nativeQuery = true)
    void updateLemmaFrequency(Integer id);

    @Query(value = "select COUNT(*) FROM search_engine.lemma where site_id=:idSiteEntity", nativeQuery = true)
    int getCountLemmasWhereSiteId(int idSiteEntity);

    @Query(value = "SELECT * FROM search_engine.lemma where lemma=:lemmaWord", nativeQuery = true) // lemma ищется по индексу
    Optional<Set<LemmaEntity>> findByLemmaWord(String lemmaWord);
}
