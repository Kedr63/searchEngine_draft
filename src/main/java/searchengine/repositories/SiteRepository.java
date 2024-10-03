package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.SiteEntity;

import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<SiteEntity, Integer> {

    @Modifying // без этой аннотации не сработает запрос на изменение таблицы
    @Query(value = "delete FROM site", nativeQuery = false)
    void deleteAllSiteEntity();

    @Modifying
    @Query(value = "delete FROM site", nativeQuery = false)
    void deleteAll();

    @Query(value = "SELECT id FROM site WHERE url like :siteBaseUrl")
    Optional<Integer> findIdSiteEntityByUrl(String siteBaseUrl);


}
