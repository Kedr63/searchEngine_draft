package searchengine.services;

import searchengine.model.IndexEntity;

import java.util.List;

public interface IndexEntityService {

    IndexEntity getIndexEntityByLemmaId(int lemmaId);

    IndexEntity getIndexEntityById(int id);

    void saveIndexEntity(IndexEntity indexEntity);

    void deleteAllIndexEntity();

    void deleteIndexEntityWherePageId(int PageId);

    List<Integer> getIdLemmaByPageId(int idPageEntity);

    List<IndexEntity> getIndexEntityListByLemmaId(int lemmaId);
}
