package searchengine.services.searchService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.dto.dtoToBD.LemmaDto;
import searchengine.services.PoolService;
import searchengine.services.indexService.lemmaParser.LemmaParserImpl;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mockito.BDDMockito.given;

public class SnippetSearcherTest {

    SearchServiceImpTest searchServiceImpTest = new SearchServiceImpTest();
    //   private final SnippetSearcher snippetSearcher = new SnippetSearcherImp();

   // private final SnippetSearcher snippetSearcher = new SnippetSearcherRegexImpl(searchServiceImpTest.getPoolService());

    PoolService poolService;


    @BeforeEach // чтоб перед каждым тестом создавалась переменная
    public void setUp(){
        poolService = searchServiceImpTest.getPoolService();

    }


    @Test
    void shouldSearchSnippets() throws IOException {

        given(poolService.getLemmaService().getNormalBaseFormWord("лежал")).willReturn("лежать");

        //File input = new File("src/test/java/searchengine/resourse/stoit-li-kupit-dom-na-kolesakh.html");
        // File input = new File("src/test/resourse/test.html");
        File input = new File("src/test/resourse/file_test.html");

        Document doc = Jsoup.parse(input, "UTF-8", "https://camper-ural.ru");

        String word = "лежал";  // для проверки poolservice

        LemmaDto lemmaDto = new LemmaDto();
        lemmaDto.setLemma("автомобиль");

        LemmaDto lemmaDtoTwo = new LemmaDto();
        lemmaDtoTwo.setLemma("прицеп");

        Set<LemmaDto> lemmaDtoSet = Set.of(lemmaDto, lemmaDtoTwo);

        String baseFormWord = poolService.getLemmaService().getNormalBaseFormWord(word);
        System.out.println(baseFormWord);

     //   String findSnippet = snippetSearcher.searchSnippets(doc, lemmaDtoSet);
      //  System.out.println(findSnippet);


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
