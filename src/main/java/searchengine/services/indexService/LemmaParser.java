package searchengine.services.indexService;

import org.jsoup.nodes.Document;
import searchengine.dto.dtoToBD.IndexDto;
import searchengine.dto.dtoToBD.LemmaDto;
import searchengine.dto.dtoToBD.PageDto;
import searchengine.dto.dtoToBD.SiteDto;
import searchengine.services.PoolService;
import searchengine.services.lemmaService.LemmaService;
import searchengine.services.utility.TextContentFromPageParser;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class LemmaParser {

    //    private final LemmaService lemmaService;
//    private final IndexEntityService indexEntityService;
    private final PoolService poolService;

    public LemmaParser(PoolService poolService) {
//        this.lemmaService = lemmaService;
//        this.indexEntityService = indexEntityService;
        this.poolService = poolService;
    }

   /* public Map<String, Integer> getLemmaFromContentPage(String contentPage) throws IOException {
        Map<String, Integer> map = new HashMap<>();
      //  String regex = ">([^><]+[^\\s ])</";
        String regex = ">(\\s*[а-яА-Яa-zA-Z\\d:;,.!-]+\\s*[а-яА-Яa-zA-Z\\d\\s:;.,!-]*)<"; // group 1 inner tags > <
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(contentPage.toLowerCase());
        while (matcher.find()) {
            String textInnerTag = matcher.group(1).replaceAll("&[a-z]+;", " ").trim(); // заберем группу 1 и избавимся от амперсанд
            textInnerTag = textInnerTag.replaceAll("[а-я]*\\.", " ").trim(); // избавимся от сокращений (например ул. пр.)
            if (!textInnerTag.isBlank()) {
                extractLemmaFromTextToMap(textInnerTag, map);
            }
        }
       // Logger.getLogger(LemmaParser.class.getName()).info("before return - map: " + map);
        return map;
    }*/

    public Map<String, Integer> getLemmasFromDocumentPage(Document document) throws IOException {
        //  Map<String, Integer> mappingLemmaToAmountOnPage = new HashMap<>();
        //  String textOfContentFromPage = document.body().text().toLowerCase();
        String textOfContentFromPage = TextContentFromPageParser.extractSemanticTextFromPage(document);
        return extractFutureLemmasFromTextForMap(textOfContentFromPage);
    }


    /* public void extractLemmaFromTextToMap(String text, Map<String, Integer> map) throws IOException {
         List<String> wordsText = Arrays.stream(text.split("[^а-яё]")).toList();
         LemmaService lemmaService = poolService.getLemmaService();
         for (String word : wordsText) {
             if (!word.isBlank() && lemmaService.hasWordInDictionary(word) && !isOfficialPartsSpeech(word) && word.length() > 1) {
                 String lemma = lemmaService.getNormalBaseFormWord(word);
                 if (map.containsKey(lemma)) {
                     map.put(lemma, map.get(lemma) + 1);
                 } else
                     map.put(lemma, 1);
             }
         }
     }*/
    public Map<String, Integer> extractFutureLemmasFromTextForMap(String text) throws IOException {
        List<String> wordsText = Arrays.stream(text.split("[^а-яё]")).toList();
        LemmaService lemmaService = poolService.getLemmaService();
        Map<String, Integer> map = new HashMap<>();
        for (String word : wordsText) {
            if (checkWord(word, lemmaService)) {
                String lemma = lemmaService.getNormalBaseFormWord(word);
                if (map.containsKey(lemma)) {
                    map.put(lemma, map.get(lemma) + 1);
                } else
                    map.put(lemma, 1);
            }

//            if (word.isBlank() || word.length() == 1) {
//                continue;
//            }
//            if (!lemmaService.hasWordInDictionary(word)) {
//                continue;
//            }
//            if (!isOfficialPartsSpeech(word)) {
//                String lemma = lemmaService.getNormalBaseFormWord(word);
//                if (map.containsKey(lemma)) {
//                    map.put(lemma, map.get(lemma) + 1);
//                } else
//                    map.put(lemma, 1);
//            }
        }
        return map;
    }

    private boolean checkWord(String word, LemmaService lemmaService) throws IOException {
        boolean isNotLemma = false;
        if (word.isBlank() || word.length() == 1) {
            return isNotLemma;
        }
        if (!lemmaService.hasWordInDictionary(word)) {
            return isNotLemma;
        }
        return !isOfficialPartsSpeech(word);
    }

    private boolean isOfficialPartsSpeech(String word) throws IOException {  // является служебной Частью Речи?
        List<String> valuesForChecking = List.of("МС", "МЕЖД", "ПРЕДЛ", "СОЮЗ", "ЧАСТ");
        List<String> stringList = poolService.getLemmaService().getMorphologyFormsInfo(word.toLowerCase());

        boolean result = false;
        for (String value : valuesForChecking) {
            result = stringList.stream().anyMatch(w -> w.contains(value));
            if (result) {
                break;
            }
        }
        return result;
    }

    public void getLemmaDtoAndSaveBD(SiteDto siteDto, PageDto pageDto, Map<String, Integer> lemmasMap) throws IOException {
        LemmaService lemmaService = poolService.getLemmaService();

        for (Map.Entry<String, Integer> entry : lemmasMap.entrySet()) {
            LemmaDto lemmaDto;
            IndexDto indexDto;
            // int lemmaId;
            synchronized (UtilitiesIndexing.lockLemmaRepository) {
                // int lemmaId = lemmaService.getLemmaId(entry.getKey(), siteEntity.getId());
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
            // TODO fix count lemma in page (now many) - DONE
            indexDto = createIndexDto(lemmaDto, pageDto, entry.getValue());
            poolService.getIndexEntityService().saveIndexDto(indexDto);
        }
    }

    private IndexDto createIndexDto(LemmaDto lemmaDto, PageDto pageDto, int ranting) {
        synchronized (UtilitiesIndexing.lockIndexLemmaService) {
            IndexDto indexDto = new IndexDto();
            indexDto.setLemmaId(lemmaDto.getId());
            indexDto.setPageId(pageDto.getId());
            indexDto.setRanting(ranting);
            return indexDto;
        }
    }

    //    public LemmaEntity createLemmaEntity(String word, SiteDto site) throws IOException {
//        //  synchronized (UtilitiesIndexing.lockLemmaRepository) {
//        LemmaEntity lemmaEntity = new LemmaEntity();
//        lemmaEntity.setLemma(word);
//        lemmaEntity.setSite(site.getId());
//        lemmaEntity.setFrequency(1);
//        return lemmaEntity;
//        // }
//    }
    public LemmaDto createLemmaDto(String word, SiteDto site) throws IOException {
        //  synchronized (UtilitiesIndexing.lockLemmaRepository) {
        LemmaDto lemmaDto = new LemmaDto();
        lemmaDto.setLemma(word);
        lemmaDto.setSiteId(site.getId());
        lemmaDto.setFrequency(1);
        return lemmaDto;
        // }
    }

    public String selectDesiredWordsInTheText(String textContentFromPage, Set<LemmaDto> lemmaDtoSetFromQuery) throws IOException {
        List<String> originalWordsFromTextList = Arrays.stream(textContentFromPage.split("[^А-Яа-яЁё]")).toList();
        Set<String> lemmaWordsFromQuerySet = lemmaDtoSetFromQuery.stream().map(LemmaDto::getLemma).collect(Collectors.toSet());
        LemmaService lemmaService = poolService.getLemmaService();
        Set<String> desiredWordsSet = new HashSet<>();

        for (String originalWord : originalWordsFromTextList) {
            //  String normalBaseFormWord = "";
            if (!checkWord(originalWord, lemmaService)) {
                continue;
            }
            String normalBaseFormFromOriginalWord = lemmaService.getNormalBaseFormWord(originalWord);
            for (String lemmaWordFromQuery : lemmaWordsFromQuerySet) {
                if (lemmaWordFromQuery.equals(normalBaseFormFromOriginalWord)) {
                    desiredWordsSet.add(originalWord);
                }
            }
        }
        return getTextWithSearchWordsBoldSelection(textContentFromPage, desiredWordsSet);
    }

    private String getTextWithSearchWordsBoldSelection(String textContentFromPage, Set<String> desiredWords) {
        StringBuilder builderRegex = new StringBuilder();
        builderRegex.append("(?<!\\p{L})").append("(");  // "(?<!\\p{L})" - это граница слова справа (/b - почему то не работает)
//        StringBuilder builderGroup = new StringBuilder();
//        int numberOfGroups = 1;
        for (String desiredWord : desiredWords) {
            builderRegex.append(makeWordWithVariableCaseFirstLetter(desiredWord)).append("|");
//            builderGroup.append("$").append(numberOfGroups);
//            numberOfGroups++;
        }
        int lengthBuilder = builderRegex.length();
        String regex = builderRegex.deleteCharAt(lengthBuilder - 1).append(")").append("(?!\\p{L})").toString(); // "(?!\\p{L})" - граница слова слева
        /* получим такую конструкцию: String regex = "(?<!\\p{L})([Лл]ес|[Дд]ом|[Мм]ашина)(?!\\p{L})";  */
      //  String replacementForRegex = builderGroup.toString();
        return textContentFromPage.replaceAll(regex, "<b>$1</b>");
//        String processedText = "";
//        for (String desiredWord : desiredWords) {
//           processedText = textContentFromPage.replaceAll(desiredWord, "<b>" + desiredWord + "</b>");
//        }
//        return processedText;
    }

    private String makeWordWithVariableCaseFirstLetter(String desiredWord) {
        String firstLetter = desiredWord.substring(0, 1);
        String remainingLettersOfWord = desiredWord.substring(1);
        String firstLetterReady = "[" + firstLetter.toUpperCase() + firstLetter.toLowerCase() + "]";
        return firstLetterReady + remainingLettersOfWord;
    }
}


