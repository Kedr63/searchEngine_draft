package searchengine.services.indexService.lemmaParser;

import org.jsoup.nodes.Document;
import searchengine.dto.dtoToBD.IndexDto;
import searchengine.dto.dtoToBD.LemmaDto;
import searchengine.dto.dtoToBD.PageDto;
import searchengine.dto.dtoToBD.SiteDto;

import java.io.IOException;
import java.util.Map;

public interface LemmaParseable {

    /**
     * @param  text в одном случае: текст из формы поисковой строки {@code Search}, в другом случае:
     *             смысловой текст (контент) со страницы
     * @return карту с отображением слова леммы к ее количеству в тексте
     * */
    Map<String, Integer> extractLemmaToAmountFromTextForMap(String text) throws IOException;
    Map<String, Integer> getLemmaWordToAmountOnPageMapFromContent(Document document) throws IOException;

    void getLemmaDtoAndIndexDto(SiteDto siteDto, PageDto pageDto, Map<String, Integer> lemmasMap) throws IOException;

    default LemmaDto createLemmaDto(String word, SiteDto site) {
        LemmaDto lemmaDto = new LemmaDto();
        lemmaDto.setLemma(word);
        lemmaDto.setSiteId(site.getId());
        lemmaDto.setFrequency(1);
        return lemmaDto;
    }

    default IndexDto createIndexDto(LemmaDto lemmaDto, PageDto pageDto, int ranting) {
        IndexDto indexDto = new IndexDto();
        indexDto.setLemmaId(lemmaDto.getId());
        indexDto.setPageId(pageDto.getId());
        indexDto.setRanting(ranting);
        return indexDto;
    }
}
