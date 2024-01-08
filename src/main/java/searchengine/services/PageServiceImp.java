package searchengine.services;

import org.springframework.stereotype.Service;
import searchengine.model.PageEntity;
import searchengine.repositories.PageRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class PageServiceImp implements PageService {

    private final PageRepository pageRepository;

    public PageServiceImp(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @Override
    @javax.transaction.Transactional
    public List<PageEntity> getAllPageEntities() {
        return pageRepository.findAll();
    }

    @Override
    @Transactional
    public void savePageEntity(PageEntity pageEntity) {
        
        pageRepository.save(pageEntity);
    }

    @Override
    @javax.transaction.Transactional
    public PageEntity getPageEntity(int id) {
        PageEntity pageEntity = null;
        Optional<PageEntity> optional = pageRepository.findById(id);
        if (optional.isPresent()){
            pageEntity = optional.get();
        }
        return pageEntity;
    }

    @Override
    @javax.transaction.Transactional
    public boolean isPresentPageEntityByPath(String path) {
        Optional<String> optional = pageRepository.findByPath(path);
        return optional.isPresent();
    }

    @Override
    @javax.transaction.Transactional
    public void deletePageEntity(int id) {
        pageRepository.deleteById(id);
    }

}
