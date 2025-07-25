package searchengine.services.indexService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.stereotype.Component;
import searchengine.dto.dtoToBD.SiteDto;
import searchengine.model.SiteEntity;
import searchengine.repositories.SiteRepository;
import searchengine.services.PoolService;

import java.util.List;

@Component
class HtmlParserTest {
    private HtmlParser htmlParser;
    private final SiteDto siteDto = new SiteDto();
    private PoolService poolService;
    private final SiteRepository siteRepository = Mockito.mock(SiteRepository.class);

    @BeforeEach
    public void setUp() {
        String url = "https://kemperus.ru/katalog_avtodomov";
        htmlParser = new HtmlParser(url, siteDto, poolService);
    }

    @Test
    @DisplayName("test isPresent")
    public void testIsPresentPathInPageRepository() {
        List<SiteEntity> siteEntities = siteRepository.findAll();

    }


}