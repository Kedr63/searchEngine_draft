package searchengine.services.searchService;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import searchengine.dto.dtoToBD.LemmaDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class SnippetSearcherImp implements SnippetSearcher {

    @Override
    public String searchSnippets(Document document, Set<LemmaDto> lemmaDtoSet) {
        List<String> tagList = List.of("h1", "h2", "h3", "p");
        List<Elements> elementsList = new ArrayList<>();
        for (String tag : tagList) {
            Elements elements = document.getElementsByTag(tag);
            elementsList.add(elements);
        }

        String result;
        StringBuilder stringBuilder = new StringBuilder();
        elementsList.forEach(element -> {
            stringBuilder.append(element.text()).append(" \n");
        });

        result = stringBuilder.toString();

     return result;
    }



}
