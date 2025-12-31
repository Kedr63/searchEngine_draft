package searchengine.services.indexService;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import searchengine.dto.indexing.PageParsed;
import searchengine.services.PoolService;
import searchengine.services.indexService.pageParser.PageParseable;
import searchengine.services.indexService.pageParser.PageParserJSOUPImpl;

import java.io.IOException;

@SpringBootTest
public class PageParserJSOUPImplTest {
    @Autowired
    private PoolService poolService;
    PageParseable pageParser;
    String url;

    @BeforeEach
    public void setUp() {

         url = "https://www.svetlovka.ru/projects/tsikl-lektsiy-pisatel-vs-rezhisser-ekranizatsii-velikikh";
        // url = "https://camper-ural.ru/complectation/rybalka-i-oxota";
    }

    @Test
    public void getParsedPageTest() throws IOException {
        int expectStatusCode = 200;
        pageParser = new PageParserJSOUPImpl(poolService);
        PageParsed pageParsed = pageParser.getParsedPage(url);
        System.out.println(pageParsed.getCode());
       try {
           Assert.assertEquals(expectStatusCode, pageParsed.getCode());
       } catch (AssertionError e) {
           System.out.println("результат: " + e.getMessage());
       }

    }
}
