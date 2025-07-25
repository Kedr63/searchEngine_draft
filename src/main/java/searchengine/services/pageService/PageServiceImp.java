package searchengine.services.pageService;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import searchengine.dto.dtoToBD.PageDto;
import searchengine.model.PageEntity;
import searchengine.repositories.PageRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PageServiceImp implements PageService {

    private final PageRepository pageRepository;
    //  private final IndexEntityService indexEntityService;
    private final ModelMapper modelMapper;
//    private final ObjectMapper objectMapper = new ObjectMapper();

//    public PageServiceImp(PageRepository pageRepository) {
//        this.pageRepository = pageRepository;
//    }

    @Override
    @Transactional
    public List<PageEntity> getAllPageEntities() {
        return pageRepository.findAll();
    }

    @Override
    @Transactional
    public PageDto savePageDto(PageDto pageDto) {
        PageEntity pageEntity = modelMapper.map(pageDto, PageEntity.class);
        PageEntity savedPageEntity = pageRepository.save(pageEntity);
        return modelMapper.map(savedPageEntity, PageDto.class);
    }

//    @Override
//    @Transactional
//    public PageEntity getPageEntityById(int id) {
//        PageEntity pageEntity = null;
//        Optional<PageEntity> optional = pageRepository.findById(id);
//        if (optional.isPresent()) {
//            pageEntity = optional.get();
//        }
//        return pageEntity;
//    }

    @Override
    @Transactional
    public PageDto getPageDtoById(int id) {
        PageDto pageDto = new PageDto();
        Optional<PageEntity> optional = pageRepository.findById(id);
        if (optional.isPresent()) {
            pageDto = modelMapper.map(optional.get(), PageDto.class);
        } else{
            pageDto.setId(0);
        }
        return pageDto;
    }

    @Override
    @Transactional
    public boolean isPresentPageEntityWithThatPath(String path, int siteId) {
        Optional<String> optional = pageRepository.findByPath(path, siteId);
        return optional.isPresent();
    }

    @Override
    @Transactional
    public void deletePageById(int id) {
        //   indexEntityService.deleteIndexEntityWherePageId(id);
        pageRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAllPageEntity() {
        Logger.getLogger(PageServiceImp.class.getName()).info("Deleting all pages    pageRepository.deleteAllPageEntity();");

        pageRepository.deleteAllPageEntity();
    }

//    @Override
//   // @Transactional
//    public void deletePageEntityWhereSiteId(int siteId) {
//        List<PageEntity> pageEntityList = pageRepository.findAll()
//                .stream()
//                .filter(p -> p.getSite().getId() == siteId).toList();
//        for (PageEntity pageEntity : pageEntityList) {
//            indexEntityService.deleteIndexEntityWherePageId(pageEntity.getId());
//        }
//        pageRepository.deletePageEntityWhereSiteId(siteId);
//    }

    @Override
    @Transactional
    public int getIdPageEntity(String pageLocalUrl, int idSiteEntity) {
        Optional<Integer> idOptional = pageRepository.findIdByPageUrlAndIdSite(pageLocalUrl, idSiteEntity);
        return idOptional.orElse(0);
    }

    @Override
    @Transactional
    public int getCountPagesOfSite(int idSiteDto) {
        return pageRepository.getCountPagesWhereSiteId(idSiteDto);
    }

}
