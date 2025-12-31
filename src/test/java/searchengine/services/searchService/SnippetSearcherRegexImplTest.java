package searchengine.services.searchService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import searchengine.dto.dtoToBD.LemmaDto;
import searchengine.services.PoolService;
import searchengine.services.indexService.lemmaParser.LemmaParserImpl;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// тест реализации интерфейса SnippetSearcher
@SpringBootTest
public class SnippetSearcherRegexImplTest {

    @Autowired // после этой аннотации подключились бины!!!!!!
    private PoolService poolService;

    LemmaDto lemmaDto;
    LemmaDto lemmaDtoTwo;
    LemmaDto lemmaDtoThree;
    Set<LemmaDto> lemmaDtoSet;

    File input;
    File input2;
    File input3;
    File input4;
    Document document;

    @BeforeEach // чтоб перед каждым тестом создавалась переменная
    public void setUp() throws IOException {

        lemmaDto = new LemmaDto();
        lemmaDto.setLemma("скидка");

        lemmaDtoTwo = new LemmaDto();
        lemmaDtoTwo.setLemma("на");

        lemmaDtoThree = new LemmaDto();
        lemmaDtoThree.setLemma("прицеп");

        lemmaDtoSet = Set.of(lemmaDto, lemmaDtoTwo, lemmaDtoThree);

        input = new File("src/test/resources/file_test.html");
        input2 = new File("src/test/resources/page_test.html");
        input3 = new File("src/test/resources/page_test_shturm.html");
        input4 = new File("src/test/resources/petsun.html");
        document = Jsoup.parse(input4, "UTF-8", "https://camper-ural.ru");
    }


    @Test
    @DisplayName("check implementation SnippetSearcherSelectorImp")
    void shouldImplementMethodOfInterfaceSnippetSearcher() throws IOException {

        SnippetSearcher snippetSearcher = new SnippetSearcherRegexImpl(poolService);
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

        LemmaParserImpl lemmaParserImpl = new LemmaParserImpl(poolService);


    }

}
