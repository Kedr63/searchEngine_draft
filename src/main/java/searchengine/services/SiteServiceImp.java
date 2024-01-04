package searchengine.services;

import org.springframework.stereotype.Service;
import searchengine.model.SiteEntity;
import searchengine.repositories.SiteRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class SiteServiceImp implements SiteService {

    private final SiteRepository siteRepository;

    public SiteServiceImp(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
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
    public void deleteAll() {
        siteRepository.deleteAll();
    }
}
