package searchengine.services.utility;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.dto.dtoToBD.LemmaDto;
import searchengine.services.PoolService;
import searchengine.services.indexService.lemmaParser.WordValidator;
import searchengine.services.lemmaService.LemmaService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TextContentFromPageHandler {

   public static String extractSemanticTextFromPage(Document document) {
       Elements mainContentOfTagBodyElement = filterElementsWithSemanticTextContentOfPage(document);
       Set<String> textSetOfTags = new LinkedHashSet<>(); // эта коллекция не примет дубликаты

       for (Element element : mainContentOfTagBodyElement) {
           recursionDeepIntoElement(element, textSetOfTags);
       }
/** Приведем /Set<String> textSetOfTags/ к виду: Главная | О компании | Полезное о жилых прицепах | Стоит ли купить дом
 *  на колесах? | Статьи | В современном мире, насыщенном стрессом и быстрой жизнью */
       StringBuilder textFromSetWithTagSeparatorBuilder = new StringBuilder();
       int sizeTextSet = textSetOfTags.size();
       int counter = 1;
       for (String text : textSetOfTags) {
           if(!text.contains(" ")){  // уберем одиночные слова текста вытянутые из тега элемента
               continue;
           }
           if (sizeTextSet != counter) {
               textFromSetWithTagSeparatorBuilder.append(text).append(" | ");
           } else {
               textFromSetWithTagSeparatorBuilder.append(text); // когда counter станет равен sizeTextSet (на
              // последнем элементе sizeTextSet)(после перебора циклом), то добавим только text без | в конце
           }
           counter++;
       }
       return textFromSetWithTagSeparatorBuilder.toString();
    }

    private static Elements filterElementsWithSemanticTextContentOfPage(Document document) {
        Elements elements = document.select("body > *"); //выберем все Element внутри body
        List<String> listTagsToDelete = List.of("header", "footer");
        int deleterElements = elements.size(); // для удаления элементов после footer
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).tagName().equals(listTagsToDelete.get(0))) {
                elements.get(i).remove();
                continue;
            }
            if (elements.get(i).tagName().equals(listTagsToDelete.get(1))) {
                elements.get(i).remove();
                deleterElements = i;
            }
            if (i > deleterElements) { // элементы после footer
                elements.get(i).remove();
            }
        }
        // проверим сколько и какие остались элементы (только после document.select увидим в bebug-е что удаленные element в loop-е
        // удалились
        //  Elements el = document.select("body > *");
        return document.select("body > *");
    }

    private static void recursionDeepIntoElement(Element element, Set<String> textSetOfTags) {
       Elements elements = element.children();
       if (!elements.isEmpty()) {
           for (Element child : elements) {
               recursionDeepIntoElement(child, textSetOfTags);
           }
       } else {
           if (!element.text().isEmpty()) {
               textSetOfTags.add(element.text());
           }
       }
    }

    public static String selectDesiredWordsInTheText(String textContentFromPage, Set<LemmaDto> lemmaDtoSetFromQuery, PoolService poolService) throws IOException {
        List<String> originalWordsFromTextList = Arrays.stream(textContentFromPage.split("[^А-Яа-яЁё]")).toList();
        Set<String> lemmaWordsFromQuerySet = lemmaDtoSetFromQuery.stream().map(LemmaDto::getLemma).collect(Collectors.toSet());
        LemmaService lemmaService = poolService.getLemmaService();
        Set<String> desiredWordsSet = new HashSet<>();

        for (String originalWord : originalWordsFromTextList) {
            //  String normalBaseFormWord = "";
            if (!WordValidator.checkWord(originalWord, lemmaService)) {
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

    /** move  TextContentFromPageHandler*/
    public static String getTextWithSearchWordsBoldSelection(String textContentFromPage, Set<String> desiredWords) {
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

//    public static List<String> splitTextContentFromPage(String textContentFromPage) {
//        return Arrays.stream(textContentFromPage.split("[^А-Яа-яё]")).toList();
//    }

    /** move  TextContentFromPageHandler*/
    private static String makeWordWithVariableCaseFirstLetter(String desiredWord) {
        String firstLetter = desiredWord.substring(0, 1);
        String remainingLettersOfWord = desiredWord.substring(1);
        String firstLetterReady = "[" + firstLetter.toUpperCase() + firstLetter.toLowerCase() + "]";
        return firstLetterReady + remainingLettersOfWord;
    }




}
