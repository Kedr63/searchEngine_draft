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

    @Query(value = "SELECT path FROM search_engine.page where path LIKE :path AND site_id =:siteId", nativeQuery = true)
    Optional<String> findByPath(String path, int siteId);

    @Modifying // без этой аннотации не сработает запрос на изменение таблицы
    @Query(value = "delete FROM page", nativeQuery = false)
    void deleteAllPageEntity();   // этот метод вместо каскадного удаления page в классе site (при каскаде запрос на удаление шел на каждую
    // страницу и получали \JAVA HEAP SPACE\

}
