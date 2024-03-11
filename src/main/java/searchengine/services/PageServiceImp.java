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
    @Transactional
    public List<PageEntity> getAllPageEntities() {
        return pageRepository.findAll();
    }

    @Override
    @Transactional
    public void savePageEntity(PageEntity pageEntity) {
        
        pageRepository.save(pageEntity);

    }

    @Override
    @Transactional
    public PageEntity getPageEntity(int id) {
        PageEntity pageEntity = null;
        Optional<PageEntity> optional = pageRepository.findById(id);
        if (optional.isPresent()){
            pageEntity = optional.get();
        }
        return pageEntity;
    }

    @Override
    @Transactional
    public boolean isPresentPageEntityByPath(String path, int siteId) {
        Optional<String> optional = pageRepository.findByPath(path, siteId);
        return optional.isPresent();
    }

    @Override
    @Transactional
    public void deletePageEntity(int id) {
        pageRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAllPageEntity() {
       // pageRepository.deleteAllPageEntity();
        pageRepository.deleteAllPageEntity();
      /*  //   pageRepository.deleteAll();
        StringBuilder stringBuilder = null;
        Logger.getLogger(PageServiceImp.class.getName()).info(" в методе - deleteAllPageEntity()");
        List<PageEntity> pageEntityList = pageRepository.findAll();
        for (PageEntity pageEntity : pageEntityList){
            stringBuilder.append(pageEntityList.indexOf(pageEntity) == 0 ? pageEntity.getId() : ", " + pageEntity.getId());
        }*/
    }

}
