package searchengine.services;

import searchengine.model.IndexEntity;

public interface IndexLemmaService {

    IndexEntity getIndexEntityByLemmaId(int lemmaId);

    IndexEntity getIndexEntityById(int id);

    void saveIndexEntity(IndexEntity indexEntity);
}
