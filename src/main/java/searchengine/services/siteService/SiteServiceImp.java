package searchengine.services.siteService;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import searchengine.dto.dtoToBD.SiteDto;
import searchengine.model.SiteEntity;
import searchengine.repositories.SiteRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
// этот слой сервис работает с данными базы по сущности SiteEntity
public class SiteServiceImp implements SiteService {

    private final SiteRepository siteRepository;
    private final ModelMapper modelMapper;


//    public SiteServiceImp(SiteRepository siteRepository) {
//        this.siteRepository = siteRepository;
//      //  this.pageService = pageService;
//    }

    @Override
    @Transactional
    public List<SiteDto> getAllSiteDto() {
        return siteRepository.findAll().stream()
                .map(siteEntity -> modelMapper.map(siteEntity, SiteDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SiteDto saveSiteDto(SiteDto siteDto) {
        SiteEntity siteEntity = modelMapper.map(siteDto, SiteEntity.class);
        SiteEntity savedSiteEntity = siteRepository.save(siteEntity);
        return modelMapper.map(savedSiteEntity, SiteDto.class);
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
    public Optional<SiteDto> getSiteDto(int id) {
        Optional<SiteEntity> optionalSiteEntity = siteRepository.findById(id);
        SiteDto siteDto = modelMapper.map(optionalSiteEntity, SiteDto.class);
        return Optional.of(siteDto);
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
    }

    @Override
    @Transactional
    public Optional<SiteDto> getSiteDtoByUrl(String domainPartUrl) {
//        Optional<SiteEntity> optionalSiteEntity = siteRepository.findAll().stream()
//                .filter(siteDto -> siteDto.getUrl().contains(domainPartUrl)).findFirst();
//        return Optional.ofNullable(modelMapper.map(optionalSiteEntity, SiteDto.class));
        return siteRepository.findAll()
                .stream()
                .filter(siteEntity -> siteEntity.getUrl().equals(domainPartUrl)).findFirst()
                .map(siteEntity -> modelMapper.map(siteEntity, SiteDto.class));
    }

    @Override
    @Transactional
    public int getIdSiteEntityByUrl(String siteBaseUrl) {
        Optional<Integer> optionalId = siteRepository.findIdSiteEntityByUrl(siteBaseUrl);
        return optionalId.orElse(0);
    }
}
