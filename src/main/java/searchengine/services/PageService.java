package searchengine.services;

import searchengine.model.PageEntity;

import java.util.List;

public interface PageService {

    List<PageEntity> getAllPageEntities();

    void savePageEntity(PageEntity pageEntity);

    PageEntity getPageEntityById(int id);

    boolean isPresentPageEntityByPath(String path, int siteId);

    void deletePageById(int id);

    void deleteAllPageEntity();

  //  void deletePageEntityWhereSiteId(int id);

    int getIdPageEntity(String pageLocalUrl, int idSiteEntity);

    int getCountPagesOfSite(int idSiteEntity);
}
