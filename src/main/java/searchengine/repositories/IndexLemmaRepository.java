package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.IndexEntity;

@Repository
public interface IndexLemmaRepository extends JpaRepository<IndexEntity, Integer> {

    @Query(value = "SELECT * FROM search_engine.indexes where lemma_id =:lemmaId", nativeQuery = true)
    IndexEntity findByLemmaId(int lemmaId);
}
