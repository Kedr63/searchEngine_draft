package searchengine.services.siteService;

import searchengine.dto.dtoToBD.SiteDto;
import searchengine.model.SiteEntity;

import java.util.List;
import java.util.Optional;

public interface SiteService {

    List<SiteDto> getAllSiteDto();

    SiteDto saveSiteDto(SiteDto siteDto);

    SiteEntity getSiteEntity(int id);

    Optional<SiteDto> getSiteDto(int id);

    void deleteAllSiteEntity();

    Optional<SiteDto> getSiteDtoByUrl(String url);
}
