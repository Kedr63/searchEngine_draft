package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.PageEntity;

import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<PageEntity, Integer> {

    //public ResponseEntity<PageEntity> findAllBy(String urlAddress);

    /*@Query(value = "SELECT path FROM search_engine.page where path LIKE :path", nativeQuery = true)
    Optional<String> findByPath(String path);*/

    @Query(value = "SELECT path FROM search_engine.page where path LIKE :path AND site_id =:siteId", nativeQuery = true)
    Optional<String> findByPath(String path, int siteId);

    @Modifying // без этой аннотации не сработает запрос на изменение таблицы
    @Query(value = "delete FROM page", nativeQuery = true)
    void deleteAllPageEntity();   // этот метод вместо каскадного удаления page в классе site (при каскаде запрос на удаление шел на каждую
    // страницу и получали \JAVA HEAP SPACE\

    @Modifying // без этой аннотации не сработает запрос на изменение таблицы
    @Query(value = "delete FROM page where site_id =:siteId", nativeQuery = true)
    void deletePageEntityWhereSiteId(int siteId);

    @Query(value = "select id from page where path LIKE :pageLocalUrl AND site_id=:idSiteEntity", nativeQuery = true)
    Optional<Integer> findIdByPageUrlAndIdSite(String pageLocalUrl, int idSiteEntity);


    @Query(value = "select count(*) FROM page WHERE site_id=:idSiteDto", nativeQuery = true)
    int getCountPagesWhereSiteId(int idSiteDto);
}
