package searchengine.services.searchService;

import org.jsoup.nodes.Document;
import searchengine.dto.dtoToBD.LemmaDto;

import java.io.IOException;
import java.util.Set;

public interface SnippetSearcher {

    String searchSnippets(Document document, Set<LemmaDto> lemmaDtoSet) throws IOException;

}
