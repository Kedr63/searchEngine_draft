package searchengine.services.searchService;

import org.jsoup.nodes.Document;
import searchengine.dto.dtoToBD.LemmaDto;
import searchengine.services.PoolService;
import searchengine.services.utility.TextContentFromPageHandler;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class SnippetSearcherRegexImpl implements SnippetSearcher {

    public PoolService poolService;

    public SnippetSearcherRegexImpl(PoolService poolService) {
        this.poolService = poolService;
    }

    @Override
    public String searchSnippets(Document document, Set<LemmaDto> lemmaDtoSetFromQuery) throws IOException {

        String textContentSetFromPageSet = TextContentFromPageHandler.extractSemanticTextFromPage(document);

       // LemmaParser lemmaParser = new LemmaParser(poolService);
        String textWithSelectedWords = TextContentFromPageHandler.selectDesiredWordsInTheText(textContentSetFromPageSet, lemmaDtoSetFromQuery, poolService);

        // Найдет в выражении все разные виды дефисов "\\p{Pd}"
        //   String regex = "((((([А-Я][а-я\\s]*)\\s*)?)|(([А-Я\\s]*)\\s*)?)((<b>[А-Яа-я]+</b>)(\\s*([\\p{Pd}а-я\\s:]*)))+)";
        //  String regex = "((((([А-Я][0-9а-я\\s]*)\\s*)?)|(([А-Яа-я\\s]*)\\s*)?)((<b>[А-Яа-я]+</b>)(\\s*([\\p{Pd}0-9а-яА-Я\\s\":]*)))+)";

        Pattern pattern = Pattern.compile(poolService.getSnippetSearcherConfiguration().getRegexSearcher());
        Matcher matcher = pattern.matcher(textWithSelectedWords);
        StringBuilder builder = new StringBuilder();
        int counter = 0;
        while (matcher.find() && counter < poolService.getSnippetSearcherConfiguration().getOutputLimiter()) {
            if (!builder.toString().contains(matcher.group())) {
                builder.append(matcher.group().trim()).append("... ").append(System.lineSeparator());
                counter++;
            }
        }
        return builder.toString();
    }


//    private String getSnippetsFromMainContent(Document document, Set<LemmaDto> lemmaDtoSet) throws IOException {
//        /*String resultTextOfContent = TextContentOfPageParser.extractTextFromPage(document);
//
//        //  String[] textArray = resultTextOfContent.split("[^а-яё]");
//        //  List<String> stringList = Arrays.stream(textArray).toList();
//        Set<String> resultWordListForSnippetSet = new HashSet<>();
//
//        LemmaParser lemmaParser = new LemmaParser(poolService);
//        Map<String, Integer> lemmaToCountMap = lemmaParser.extractFutureLemmasFromTextForMap(resultTextOfContent);
//
//        resultWordListForSnippetSet = lemmaToCountMap.keySet();
//
//        for (String str : resultWordListForSnippetSet) {
//            resultTextOfContent = resultTextOfContent.replaceAll(str, "<b>" + str + "</b>");
//        }
//
//        String regex = "(((^[\\W\\s]+)|[А-Яа-я\\s]*)\\s*(<b>[А-Яа-я]+</b>)(\\s*)([А-Яа-я]*\\s*[\\S]*\\.?))";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(resultTextOfContent);
//        StringBuilder builder = new StringBuilder();
//        while (matcher.find()) {
//            builder.append(matcher.group().trim()).append("...").append("\n");
//        }*/
//
//        //TODO find bug and fix
//     //   String result = resultWordListForSnippetSet.stream().map(str -> resultTextOfContent.replace(str, "<b>" + str + "</b>")).toString();
//
//        return builder.toString();
//    }
}
