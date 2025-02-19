package searchengine.services.siteService;

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
    @Transactional
    public List<SiteEntity> getAllSiteEntities() {
        return siteRepository.findAll();
    }

    @Override
    @Transactional
    public void saveSiteEntity(SiteEntity siteEntity) {
        siteRepository.save(siteEntity);
    }

    @Override
    @Transactional
    public SiteEntity getSiteEntity(int id) {
        Optional<SiteEntity> optionalSiteEntity = siteRepository.findById(id);
        SiteEntity siteEntity = new SiteEntity();
        if (optionalSiteEntity.isPresent()) {
            siteEntity = optionalSiteEntity.get();
        }
        return siteEntity;
    }

    @Override
    @Transactional
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
    public SiteEntity getSiteEntityByUrl(String domainPartUrl) throws NoSuchElementException {
       Optional<SiteEntity> optionalSiteEntity = siteRepository.findAll().stream()
               .filter(siteEntity -> siteEntity.getUrl().contains(domainPartUrl)).findFirst();
       if (optionalSiteEntity.isPresent()) {
           return optionalSiteEntity.get();
       } else throw new NoSuchElementException();
    }

    @Override
    @Transactional
    public int getIdSiteEntityByUrl(String siteBaseUrl) {
        Optional<Integer> optionalId = siteRepository.findIdSiteEntityByUrl(siteBaseUrl);
        return optionalId.orElse(0);
    }
}
