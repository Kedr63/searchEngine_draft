package searchengine.services.indexService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import searchengine.dto.dtoToBD.SiteDto;
import searchengine.services.PoolService;

import java.io.IOException;

@SpringBootTest
class HtmlRecursiveParserTest {
    HtmlRecursiveParser htmlRecursiveParser;
    private SiteDto siteDto;
    @Autowired
    private PoolService poolService;
    //  private final SiteRepository siteRepository = Mockito.mock(SiteRepository.class);

    @BeforeEach
    public void setUp() {
//        String url = "https://kemperus.ru/katalog_avtodomov";
//        htmlRecursiveParser = new HtmlRecursiveParser(url, siteDto, poolService);

        String url = "https://www.svetlovka.ru/projects/tsikl-lektsiy-pisatel-vs-rezhisser-ekranizatsii-velikikh";
        siteDto = new SiteDto();
        siteDto.setUrl(url);
        htmlRecursiveParser = new HtmlRecursiveParser(url, siteDto, poolService);


    }

    @Test
    @DisplayName("test isPresent")
    public void testIsPresentPathInPageRepository() {
        //  List<SiteEntity> siteEntities = siteRepository.findAll();

    }

    @Test
    public void testGetParsedPage() throws IOException {
        //  HtmlRecursiveParser htmlRecursiveParser1 = new HtmlRecursiveParser();
      //  Method myMethod = HtmlRecursiveParser.class.getDeclaredMethod("getParsedPage");


    }

}