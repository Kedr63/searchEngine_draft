package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import searchengine.model.PageEntity;

import java.util.Optional;

public interface PageRepository extends JpaRepository<PageEntity, Integer> {

    //public ResponseEntity<PageEntity> findAllBy(String urlAddress);

    /*@Query(value = "SELECT path FROM search_engine.page where path LIKE :path", nativeQuery = true)
    Optional<String> findByPath(String path);*/

    @Query(value = "SELECT path FROM search_engine.page where path LIKE :path", nativeQuery = true)
    Optional<String> findByPath(String path);

    @Modifying
    @Query(value = "delete FROM page", nativeQuery = false)
    void deleteAllPageEntity();

}
