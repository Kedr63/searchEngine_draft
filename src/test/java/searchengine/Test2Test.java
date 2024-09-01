package searchengine;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import searchengine.services.LemmaServiceImpl;
import searchengine.services.indexService.LemmaParser;
import searchengine.services.indexService.PoolServiceImpl;

import java.io.IOException;

public class Test2Test {

    LemmaServiceImpl lemmasService = Mockito.mock(LemmaServiceImpl.class);
    PoolServiceImpl poolService = Mockito.mock(PoolServiceImpl.class);
    public LemmaParser lemmaParser;
    LuceneMorphology luceneMorphology = new RussianLuceneMorphology();

    public Test2Test() throws IOException {
    }


    @BeforeEach // чтоб перед каждым тестом создавалась переменная
    public void setUp() {

        lemmaParser = new LemmaParser(poolService);
    }
}
