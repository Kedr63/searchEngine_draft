package searchengine.services.siteService;

import searchengine.model.SiteEntity;

import java.util.List;

public interface SiteService {

    List<SiteEntity> getAllSiteEntities();

    void saveSiteEntity(SiteEntity siteEntity);

    SiteEntity getSiteEntity(int id);

    void deleteSiteEntity(int id);

    void deleteAllSiteEntity();

    SiteEntity getSiteEntityByUrl(String url);

    int getIdSiteEntityByUrl(String siteBaseUrl);
}
