package searchengine.services.indexService;

import searchengine.model.IndexEntity;
import searchengine.model.LemmaEntity;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.services.IndexEntityService;
import searchengine.services.LemmaService;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LemmaParser {

    private final LemmaService lemmaService;
    private final IndexEntityService indexEntityService;

    public LemmaParser(LemmaService lemmaService, IndexEntityService indexEntityService) {
        this.lemmaService = lemmaService;
        this.indexEntityService = indexEntityService;
    }

    public Map<String, Integer> getLemmaFromContentPage(String contentPage) throws IOException {
        Map<String, Integer> map = new HashMap<>();
        String regex = ">([^><]+)</";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(contentPage.toLowerCase());
        while (matcher.find()) {
            String textInnerTag = matcher.group(1).replaceAll("&[a-z]+;", " ").trim();
            if (!textInnerTag.isBlank()) {
                List<String> wordsText = Arrays.stream(textInnerTag.split("[^а-яё]")).toList();
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
        Logger.getLogger(LemmaParser.class.getName()).info("before return - map: " + map);
        return map;
    }

    private boolean isOfficialPartsSpeech(String word) throws IOException {  // является служебной Частью Речи
        List<String> valuesForChecking = List.of("МЕЖД", "ПРЕДЛ", "СОЮЗ", "Н");
        List<String> stringList = lemmaService.getMorphologyForms(word);
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
            synchronized (UtilitiesIndexing.lockLemmaRepository){
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
            indexEntityService.saveIndexEntity(indexEntity);
        }
    }

    private IndexEntity createIndexEntity(LemmaEntity lemmaEntity, PageEntity pageEntity, int ranting) {
        synchronized (UtilitiesIndexing.lockIndexLemmaService) {
            IndexEntity indexEntity = new IndexEntity();
            indexEntity.setLemma(lemmaEntity);
            indexEntity.setPage(pageEntity);
            indexEntity.setRanting(ranting);
            return indexEntity;
        }
    }

    public LemmaEntity createLemmaEntity(String word, SiteEntity site) throws IOException {
        synchronized (UtilitiesIndexing.lockLemmaRepository) {
            LemmaEntity lemmaEntity = new LemmaEntity();
            lemmaEntity.setLemma(word);
            lemmaEntity.setSite(site);
            lemmaEntity.setFrequency(1);
            return lemmaEntity;
        }
    }


}
