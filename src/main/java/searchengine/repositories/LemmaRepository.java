package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.LemmaEntity;

import java.util.Optional;

@Repository
public interface LemmaRepository extends JpaRepository<LemmaEntity, Integer> {

    @Query(value = "SELECT lemma FROM search_engine.lemma where lemma LIKE :lemma AND site_id =:siteId", nativeQuery = true)
    Optional<String> findByLemma(String lemma, int siteId);

    @Query(value = "SELECT id FROM search_engine.lemma where lemma LIKE :lemma AND site_id =:siteId", nativeQuery = true)
    Optional<Integer> findIdByLemma(String lemma, int siteId);
}
