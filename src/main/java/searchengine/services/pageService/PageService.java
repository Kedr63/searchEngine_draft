package searchengine.services.pageService;

import searchengine.dto.dtoToBD.PageDto;
import searchengine.model.PageEntity;

import java.util.List;

public interface PageService {

    List<PageEntity> getAllPageEntities();

    PageDto savePageDto(PageDto pageDto);

 //   PageEntity getPageEntityById(int id);

    PageDto getPageDtoById(int id);

    boolean isPresentPageEntityWithThatPath(String path, int siteId);

    void deletePageById(int id);

    void deleteAllPageEntity();

  //  void deletePageEntityWhereSiteId(int id);

    int getIdPageEntity(String pageLocalUrl, int idSiteEntity);

    int getCountPagesOfSite(int idSiteDto);
}
