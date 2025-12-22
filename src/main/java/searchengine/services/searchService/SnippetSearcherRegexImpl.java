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

    /** Сниппеты из текста будут искаться с помощью регулярного выражения заданного в конфигурационном файле {@code application.yaml}
     * <p>Дополнительная информация: <a href="https://regex101.com/r/jQ2kzt/3"> Как работает это регулярное выражение
     * на сервисе {@code regex101.com}</a></p>
     *
     * */
    @Override
    public String searchSnippets(Document document, Set<LemmaDto> lemmaDtoSetFromQuery) throws IOException {

        String textContentSetFromPageSet = TextContentFromPageHandler.extractSemanticTextFromPage(document);
        String textWithSelectedWords = TextContentFromPageHandler.selectDesiredWordsInTheText(textContentSetFromPageSet, lemmaDtoSetFromQuery, poolService);

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
}
