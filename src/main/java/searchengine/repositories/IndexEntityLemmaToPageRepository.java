package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.IndexEntity;

import java.util.List;
import java.util.Set;

@Repository
public interface IndexEntityLemmaToPageRepository extends JpaRepository<IndexEntity, Integer> {

    @Query(value = "SELECT * FROM search_engine.index_search where lemma_id=:lemmaId", nativeQuery = true)
    IndexEntity findByLemmaId(int lemmaId);

    @Modifying // без этой аннотации не сработает запрос на изменение таблицы
    @Query(value = "delete FROM search_engine.index_search", nativeQuery = true)
    void deleteAllIndexEntity();

    @Modifying
    @Query(value = "delete from search_engine.index_search where page_id =:pageId", nativeQuery = true)
    void deleteIndexEntityWherePageId(int pageId);

    @Query(value = "select lemma_id from search_engine.index_search where page_id=:idPage", nativeQuery = true)
    List<Integer> findIdLemmaByIdPage(int idPage);

    @Query(value = "select * from search_engine.index_search where lemma_id=:lemmaId", nativeQuery = true)
    Set<IndexEntity> findIndexEntityBy(int lemmaId);

    @Query(value = "select page_id from index_search where lemma_id=:lemmaId", nativeQuery = true)
    Set<Integer> findPageIdByLemmaId(int lemmaId);

    @Query(value = "SELECT ranting FROM search_engine.index_search where lemma_id=:lemmaId and page_id=:pageId", nativeQuery = true)
    float findRankByLemmaIdAndPageId(int lemmaId, int pageId);
}
