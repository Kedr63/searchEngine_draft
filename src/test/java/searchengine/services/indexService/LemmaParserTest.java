package searchengine.services.indexService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import searchengine.services.PoolServiceImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


class LemmaParserTest {
    PoolServiceImpl poolService = Mockito.mock(PoolServiceImpl.class);
    public LemmaParser lemmaParser;


    /* здесь будет создаваться (инициализироваться) переменная сверху */
    @BeforeEach // чтоб перед каждым тестом создавалась переменная
    public void setUp() {
        lemmaParser = new LemmaParser(poolService);
    }

    @Test
    @DisplayName("Test extractLemmaFromTextToMap")
    public void testExtractLemmasFromPageTextForMap() throws IOException {
        String text = "купить кемпер для путешествий";
        Map<String, Integer> map = new HashMap<>();
        Set<String> stringSet = map.keySet();
    //    lemmaParser.extractLemmaFromTextToMap(text, map);


    }


}