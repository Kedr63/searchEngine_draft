package searchengine.services.indexService;

import searchengine.model.IndexEntity;
import searchengine.model.LemmaEntity;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.services.LemmaService;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LemmaParser {

    //    private final LemmaService lemmaService;
//    private final IndexEntityService indexEntityService;
    private final PoolService poolService;

    public LemmaParser(PoolService poolService) {
//        this.lemmaService = lemmaService;
//        this.indexEntityService = indexEntityService;
        this.poolService = poolService;
    }

    public Map<String, Integer> getLemmaFromContentPage(String contentPage) throws IOException {
        Map<String, Integer> map = new HashMap<>();
        String regex = ">([^><]+)</";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(contentPage.toLowerCase());
        while (matcher.find()) {
            String textInnerTag = matcher.group(1).replaceAll("&[a-z]+;", " ").trim();
            if (!textInnerTag.isBlank()) {
                extractLemmaFromTextToMap(textInnerTag, map);
            }
        }
       // Logger.getLogger(LemmaParser.class.getName()).info("before return - map: " + map);
        return map;
    }

    private boolean isOfficialPartsSpeech(String word) throws IOException {  // является служебной Частью Речи
        List<String> valuesForChecking = List.of("МЕЖД", "ПРЕДЛ", "СОЮЗ", "Н");
        List<String> stringList = poolService.getLemmaService().getMorphologyForms(word);
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
                    lemmaEntity = lemmaService.getLemmaEntity(lemmaId);
                    lemmaEntity.setFrequency(lemmaEntity.getFrequency() + 1);
                    lemmaService.saveLemmaEntity(lemmaEntity);
                }
            }
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

    public void extractLemmaFromTextToMap(String text, Map<String, Integer> map) throws IOException {
        List<String> wordsText = Arrays.stream(text.split("[^а-яё]")).toList();
        LemmaService lemmaService = poolService.getLemmaService();
        for (String word : wordsText) {
            if (!word.isBlank() && lemmaService.isValidWord(word) && !isOfficialPartsSpeech(word)) {
                String lemma = lemmaService.getNormalBaseFormWord(word);
                if (map.containsKey(lemma)) {
                    map.put(lemma, map.get(lemma) + 1);
                } else
                    map.put(lemma, 1);
            }
        }
    }


}
