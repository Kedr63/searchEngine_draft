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

@Service
@RequiredArgsConstructor
public class PageServiceImp implements PageService {

    private final PageRepository pageRepository;
    private final ModelMapper modelMapper;

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
        Optional<String> optionalFoundPath = pageRepository.findByPath(path, siteId);
        return optionalFoundPath.isPresent();
    }

    @Override
    @Transactional
    public void deletePageById(int id) {
        pageRepository.deleteById(id);
    }

    /** Этот метод вместо каскадного удаления {@code PageEntity} в классе {@code SiteEntity} (при каскаде запрос
     * на удаление шел на каждую страницу и получали <b><i>JAVA HEAP SPACE</i></b>)
     */
    @Override
    @Transactional
    public void deleteAllPageEntity() {
        pageRepository.deleteAllPageEntity();
    }

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
