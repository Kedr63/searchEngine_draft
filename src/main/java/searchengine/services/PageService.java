package searchengine.services;

import searchengine.model.PageEntity;

import java.util.List;

public interface PageService {

    List<PageEntity> getAllPageEntities();

    void savePageEntity(PageEntity pageEntity);

    PageEntity getPageEntity(int id);

    boolean isPresentPageEntityByPath(String path);

    void deletePageEntity(int id);

}
