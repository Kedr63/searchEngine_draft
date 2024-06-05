package searchengine.services;

import org.springframework.stereotype.Service;
import searchengine.model.SiteEntity;
import searchengine.repositories.SiteRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class SiteServiceImp implements SiteService {

    private final SiteRepository siteRepository;
  //  private final PageService pageService;


    public SiteServiceImp(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
      //  this.pageService = pageService;
    }

    @Override
    @javax.transaction.Transactional
    public List<SiteEntity> getAllSiteEntities() {
        return siteRepository.findAll();
    }

    @Override
    @javax.transaction.Transactional
    public void saveSiteEntity(SiteEntity siteEntity) {
        siteRepository.save(siteEntity);
    }

    @Override
    @javax.transaction.Transactional
    public SiteEntity getSiteEntity(int id) {
        SiteEntity siteEntity = null;
        Optional<SiteEntity> optional = siteRepository.findById(id);
        if (optional.isPresent()) {
            siteEntity = optional.get();
        }
        return siteEntity;
    }

    @Override
    @javax.transaction.Transactional
    public void deleteSiteEntity(int id) {
        siteRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAllSiteEntity() {
        Logger.getLogger(SiteServiceImp.class.getName()).info(" в методе - deleteAll   siteRepository.deleteAll()");
         siteRepository.deleteAllSiteEntity();
//        List<SiteEntity> siteEntities = siteRepository.findAll();
//        for (SiteEntity siteEntity : siteEntities) {
//            pageService.deletePageEntityWhereSiteId(siteEntity.getId());
//            siteRepository.delete(siteEntity);
//        }
    }

    @Override
    @Transactional
    public SiteEntity getSiteEntityByUrl(String url) throws NoSuchElementException {
       Optional<SiteEntity> optionalSiteEntity = siteRepository.findAll().stream()
               .filter(siteEntity -> siteEntity.getUrl().contains(url)).findFirst();
        return optionalSiteEntity.orElse(null);
    }

    @Override
    @Transactional
    public int getIdSiteEntityByUrl(String siteBaseUrl) {
        Optional<Integer> optionalId = siteRepository.findIdSiteEntityByUrl(siteBaseUrl);
        return optionalId.orElse(0);
    }
}
