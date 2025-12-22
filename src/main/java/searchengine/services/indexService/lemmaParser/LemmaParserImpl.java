package searchengine.services.indexService.lemmaParser;

import org.jsoup.nodes.Document;
import searchengine.dto.dtoToBD.IndexDto;
import searchengine.dto.dtoToBD.LemmaDto;
import searchengine.dto.dtoToBD.PageDto;
import searchengine.dto.dtoToBD.SiteDto;
import searchengine.services.PoolService;
import searchengine.services.indexService.UtilitiesIndexing;
import searchengine.services.lemmaService.LemmaService;
import searchengine.services.utility.TextContentFromPageHandler;

import java.io.IOException;
import java.util.*;

public class LemmaParserImpl implements LemmaParseable {

    private final PoolService poolService;

    public LemmaParserImpl(PoolService poolService) {
        this.poolService = poolService;
    }

    /**
     * Из Document пропарсенной страницы, метод извлечет леммы с отображением их количества на странице. Рабатать будет
     * только с семантически смысловым текстом страницы
     */
    public Map<String, Integer> getLemmaToAmountOnPageMapFromContentOfDocument(Document document) throws IOException {
        String textOfContentFromPage = TextContentFromPageHandler.extractSemanticTextFromPage(document);
        return extractLemmaToAmountFromTextForMap(textOfContentFromPage);
    }


    public Map<String, Integer> extractLemmaToAmountFromTextForMap(String text) throws IOException {
        List<String> wordsText = Arrays.stream(text.split("[^А-Яа-яё]")).toList();
        LemmaService lemmaService = poolService.getLemmaService();
        Map<String, Integer> map = new HashMap<>();
        for (String word : wordsText) {
            if (WordValidator.checkWord(word, lemmaService)) {
                String lemma = lemmaService.getNormalBaseFormWord(word);
                if (map.containsKey(lemma)) {
                    map.put(lemma, map.get(lemma) + 1);
                } else
                    map.put(lemma, 1);
            }
        }
        return map;
    }

//    @NotNull
//    private Map<String, Integer> getLemmaToAmountMap(String text) throws IOException {
//        List<String> wordsText = Arrays.stream(text.split("[^А-Яа-яё]")).toList();
//        LemmaService lemmaService = poolService.getLemmaService();
//        Map<String, Integer> map = new HashMap<>();
//        for (String word : wordsText) {
//            if (WordValidator.checkWord(word, lemmaService)) {
//                String lemma = lemmaService.getNormalBaseFormWord(word);
//                if (map.containsKey(lemma)) {
//                    map.put(lemma, map.get(lemma) + 1);
//                } else
//                    map.put(lemma, 1);
//            }
//        }
//        return map;
//    }

    public void getLemmaDtoIndexDto(SiteDto siteDto, PageDto pageDto, Map<String, Integer> lemmasMap) throws IOException {
        LemmaService lemmaService = poolService.getLemmaService();

        for (Map.Entry<String, Integer> entry : lemmasMap.entrySet()) {
            LemmaDto lemmaDto;
            IndexDto indexDto;
            // int lemmaId;
            synchronized (UtilitiesIndexing.lockLemmaRepository) {
                Optional<Integer> optionalLemmaId = lemmaService.getLemmaId(entry.getKey(), siteDto.getId());
                if (optionalLemmaId.isEmpty()) {
                    lemmaDto = createLemmaDto(entry.getKey(), siteDto);
                    lemmaDto = lemmaService.saveLemmaDto(lemmaDto);
                } else {
                    lemmaDto = lemmaService.getLemmaDtoById(optionalLemmaId.get());

                    lemmaDto.setFrequency(lemmaDto.getFrequency() + 1);
                    lemmaDto = lemmaService.saveLemmaDto(lemmaDto);
                }
            }
            indexDto = createIndexDto(lemmaDto, pageDto, entry.getValue());
            poolService.getIndexEntityService().saveIndexDto(indexDto);
        }
    }
}


