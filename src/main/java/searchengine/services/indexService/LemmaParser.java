package searchengine.services.indexService;

import org.jsoup.nodes.Document;
import searchengine.model.IndexEntity;
import searchengine.model.LemmaEntity;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.services.PoolService;
import searchengine.services.lemmaService.LemmaService;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String, Integer> getLemmaFromDocumentPage(Document document) throws IOException {
        Map<String, Integer> mappingLemmaToAmountOnPage = new HashMap<>();
        String textOfContentFromPage = document.body().text().toLowerCase();
        extractLemmasFromPageTextForMap(textOfContentFromPage, mappingLemmaToAmountOnPage);
        return mappingLemmaToAmountOnPage;
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
   public void extractLemmasFromPageTextForMap(String text, Map<String, Integer> map) throws IOException {
       List<String> wordsText = Arrays.stream(text.split("[^а-яё]")).toList();
       LemmaService lemmaService = poolService.getLemmaService();
       for (String word : wordsText) {
           if (word.isBlank() || word.length()==1){
               continue;
           }
           if (!lemmaService.hasWordInDictionary(word)){
               continue;
           }
           if (!isOfficialPartsSpeech(word)){
               String lemma = lemmaService.getNormalBaseFormWord(word);
               if (map.containsKey(lemma)) {
                   map.put(lemma, map.get(lemma) + 1);
               } else
                   map.put(lemma, 1);
           }
       }
   }

    private boolean isOfficialPartsSpeech(String word) throws IOException {  // является служебной Частью Речи?
        List<String> valuesForChecking = List.of("МС", "МЕЖД", "ПРЕДЛ", "СОЮЗ", "Н", "ЧАСТ");
        List<String> stringList = poolService.getLemmaService().getMorphologyFormsInfo(word);

        boolean result = false;
        for (String value : valuesForChecking) {
            result = stringList.stream().anyMatch(w -> w.contains(value));
            if (result) {
                break;
            }
        }
        return result;
    }

    public void getLemmaEntitiesAndSaveBD(SiteEntity siteEntity, PageEntity pageEntity, Map<String, Integer> lemmasMap) throws IOException {
        for (Map.Entry<String, Integer> entry : lemmasMap.entrySet()) {
            LemmaEntity lemmaEntity;
            IndexEntity indexEntity;
            int lemmaId;
            synchronized (UtilitiesIndexing.lockLemmaRepository) {
                LemmaService lemmaService = poolService.getLemmaService();
                lemmaId = lemmaService.getLemmaId(entry.getKey(), siteEntity.getId());
                if (lemmaId == 0) {
                    lemmaEntity = createLemmaEntity(entry.getKey(), siteEntity);
                    lemmaService.saveLemmaEntity(lemmaEntity);
                } else {
                    lemmaEntity = lemmaService.getLemmaEntityById(lemmaId);
                    lemmaEntity.setFrequency(lemmaEntity.getFrequency() + 1);
                    lemmaService.saveLemmaEntity(lemmaEntity);
                }
            }
            // TODO fix count lemma in page (now many)
            indexEntity = createIndexEntity(lemmaEntity, pageEntity, entry.getValue());
            poolService.getIndexEntityService().saveIndexEntity(indexEntity);
        }
    }

    private IndexEntity createIndexEntity(LemmaEntity lemmaEntity, PageEntity pageEntity, int ranting) {
        synchronized (UtilitiesIndexing.lockIndexLemmaService) {
            IndexEntity indexEntity = new IndexEntity();
            indexEntity.setLemma(lemmaEntity);
            indexEntity.setPageEntity(pageEntity);
            indexEntity.setRanting(ranting);
            return indexEntity;
        }
    }

    public LemmaEntity createLemmaEntity(String word, SiteEntity site) throws IOException {
        //  synchronized (UtilitiesIndexing.lockLemmaRepository) {
            LemmaEntity lemmaEntity = new LemmaEntity();
            lemmaEntity.setLemma(word);
            lemmaEntity.setSite(site);
            lemmaEntity.setFrequency(1);
            return lemmaEntity;
       // }
    }

 /*   private static String getBaseFormFromNormalForms(String word, List<String> wordNormalForms) {
        String baseFormWord = "";
        for (String wordNormalForm : wordNormalForms) {
            if (wordNormalForm.compareTo(word) == 0) {
                baseFormWord = wordNormalForm;
                break;
            }
        }
        if (baseFormWord.isEmpty()) {
            baseFormWord = wordNormalForms.get(0);
        }
        return baseFormWord;
    }*/


}
