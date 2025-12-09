package searchengine.services.indexService.lemmaParser;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import searchengine.services.lemmaService.LemmaService;

import java.io.IOException;
import java.util.List;

@Setter//
@RequiredArgsConstructor
public class WordValidator {

    public static boolean checkWord(String word, LemmaService lemmaService) throws IOException {
        boolean isNotLemma = false;
        if (word.isBlank() || word.length() == 1) {
            return isNotLemma;
        }
        if (!lemmaService.hasWordInDictionary(word)) {
            return isNotLemma;
        }
        return !isOfficialPartsSpeech(word, lemmaService);
    }

    private static boolean isOfficialPartsSpeech(String word, LemmaService lemmaService) throws IOException {  // является служебной Частью Речи?
        List<String> valuesForChecking = List.of("МС", "МЕЖД", "ПРЕДЛ", "СОЮЗ", "ЧАСТ");
        List<String> stringList = lemmaService.getMorphologyFormsInfo(word.toLowerCase());

        boolean result = false;
        for (String value : valuesForChecking) {
            result = stringList.stream().anyMatch(w -> w.contains(value));
            if (result) {
                break;
            }
        }
        return result;
    }

}
