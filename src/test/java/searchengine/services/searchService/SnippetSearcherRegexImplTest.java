package searchengine.services.searchService;

import org.apache.lucene.morphology.LuceneMorphology;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import searchengine.config.SnippetSearcherConfiguration;
import searchengine.config.UserAgentList;
import searchengine.dto.dtoToBD.LemmaDto;
import searchengine.repositories.LemmaRepository;
import searchengine.services.PoolService;
import searchengine.services.PoolServiceImpl;
import searchengine.services.factoryObject.LuceneMorphologyFactory;
import searchengine.services.indexEntityService.IndexEntityServiceImpl;
import searchengine.services.indexService.lemmaParser.LemmaParser;
import searchengine.services.lemmaService.LemmaService;
import searchengine.services.lemmaService.LemmaServiceImpl;
import searchengine.services.pageService.PageServiceImp;
import searchengine.services.siteService.SiteServiceImp;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// тест реализации интерфейса SnippetSearcher
public class SnippetSearcherRegexImplTest {

    SnippetSearcher snippetSearcher;
    PoolService poolService;
    LuceneMorphology luceneMorphology;
    LemmaService lemmaService;
    ModelMapper modelMapper;
    SnippetSearcherConfiguration snippetSearcherConfiguration;

    LemmaDto lemmaDto;
    LemmaDto lemmaDtoTwo;
    LemmaDto lemmaDtoThree;
    Set<LemmaDto> lemmaDtoSet;

    File input;
    Document document;

    @BeforeEach // чтоб перед каждым тестом создавалась переменная
    public void setUp() throws IOException {
        //  poolService = PoolServiceMockitoFactory.getPoolService();
        modelMapper = new ModelMapper();
        luceneMorphology = LuceneMorphologyFactory.getLuceneMorphologyFactory();
        lemmaService = new LemmaServiceImpl(luceneMorphology, Mockito.mock(LemmaRepository.class), modelMapper);
        poolService = new PoolServiceImpl(Mockito.mock(SiteServiceImp.class), Mockito.mock(PageServiceImp.class),
                lemmaService, Mockito.mock(IndexEntityServiceImpl.class), Mockito.mock(UserAgentList.class),
                Mockito.mock(SnippetSearcherConfiguration.class));
        snippetSearcher = new SnippetSearcherRegexImpl(poolService);


        lemmaDto = new LemmaDto();
        lemmaDto.setLemma("дом");

        lemmaDtoTwo = new LemmaDto();
        lemmaDtoTwo.setLemma("прицеп");

        lemmaDtoThree = new LemmaDto();
        lemmaDtoThree.setLemma("купить");

        lemmaDtoSet = Set.of(lemmaDto, lemmaDtoTwo, lemmaDtoThree);

        input = new File("src/test/resourse/file_test.html");
        document = Jsoup.parse(input, "UTF-8", "https://camper-ural.ru");
    }


    @Test
    @DisplayName("check implementation SnippetSearcherSelectorImp")
    void shouldImplementMethodOfInterfaceSnippetSearcher() throws IOException {

//        given(poolService.getLemmaService().getNormalBaseFormWord("лежал")).willReturn("лежать");
//        String word = "лежал";  // для проверки poolservice in Debug
//        String expectWord = "лежать";
//        String baseFormWordExpect = poolService.getLemmaService().getNormalBaseFormWord(word);
//        System.out.println(baseFormWordExpect);
//        Assertions.assertEquals(baseFormWordExpect, expectWord);

        String findSnippet = snippetSearcher.searchSnippets(document, lemmaDtoSet);
        System.out.println(findSnippet);


    }

    @Test
    void shouldFindLemmaInText() throws IOException {
        Set<String> resultWordListForSnippetSet = Set.of("автомобиль", "море");
        String resultTextOfContent = "Описание почему нужно покупать автомобиль для путешествий по стране и на море. Какой автомобиль покупать для семьи\n" +
                "\tчтоб было комфортно. Как выгодней купить автомобиль. Какие бывают марки автомобилей. Плюсы путешествий на машине.";

        String word = "море";
        String word1 = "автомобиль";
        //  String regex = word + "|" + word1;
        //    String result = resultWordListForSnippetSet.stream().anyMatch(str-> resultTextOfContent.replaceAll(str, "<b>" + str + "</b>"));
        // resultWordListForSnippetSet
        //        .forEach(str -> resultTextOfContent.replace(str, "<b>" + str + "</b>"));


        /* работает перебором */
        for (String str : resultWordListForSnippetSet) {
            resultTextOfContent = resultTextOfContent.replaceAll(str, "<b>" + str + "</b>");
        }

        String regex = "(((^[\\W\\s]+)|[А-Яа-я\\s]*)\\s*(<b>[А-Яа-я]+</b>)(\\s*)([А-Яа-я]*\\s*[\\S]*\\.?))";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(resultTextOfContent);
        StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            builder.append(matcher.group().trim()).append("...").append(System.lineSeparator());
        }

        String result = builder.toString();


        System.out.println(result);
    }

    @Test
    void shouldSelectLemmaInText() throws IOException {
        Set<String> resultWordListForSnippetSet = Set.of("автомобиль", "море");
        String resultTextOfContent = "Описание почему нужно покупать автомобиль для путешествий по стране и на море. Какой автомобиль покупать для семьи\n" +
                "\tчтоб было комфортно. Как выгодней купить автомобиль. Какие бывают марки автомобилей. Плюсы путешествий на машине.";

        String word = "море";
        String word1 = "автомобиль";

        LemmaParser lemmaParser = new LemmaParser(poolService);


    }

}
