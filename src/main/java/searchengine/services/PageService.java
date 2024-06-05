package searchengine.services;

import searchengine.model.PageEntity;

import java.util.List;

public interface PageService {

    List<PageEntity> getAllPageEntities();

    void savePageEntity(PageEntity pageEntity);

    PageEntity getPageEntity(int id);

    boolean isPresentPageEntityByPath(String path, int siteId);

    void deletePageEntity(int id);

    void deleteAllPageEntity();

  //  void deletePageEntityWhereSiteId(int id);

    int getIdPageEntity(String pageLocalUrl, int idSiteEntity);

    int getCountPagesOfSite(int idSiteEntity);
}
