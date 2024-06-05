package searchengine.services.indexService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import searchengine.model.SiteEntity;
import searchengine.repositories.SiteRepository;

import java.util.List;

@Component
class HtmlParserTest {
  private HtmlParser htmlParser;
    private final SiteEntity siteEntity = new SiteEntity();
    private PoolService poolService;
    private SiteRepository siteRepository;

  @BeforeEach
  public void setUp(){
      String url = "https://kemperus.ru/katalog_avtodomov";
      htmlParser = new HtmlParser(url, siteEntity, poolService);
  }

  @Test
  @DisplayName("test isPresent")
  public void testIsPresentPathInPageRepository(){
      List<SiteEntity> siteEntities= siteRepository.findAll();

  }


}