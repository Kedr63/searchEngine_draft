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

    /**Выберет из страницы только семантически нужный текст контент и строитель соберет его для удобства обработки
     * Приведем {@code Set<String> textSetOfTags} к виду: <b><i>Главная | О компании | Полезное о жилых прицепах | Стоит
     * ли купить дом на колесах? | Статьи | В современном мире, насыщенном</i></b> */
    public static String extractSemanticTextFromPage(Document document) {
        Elements mainContentOfTagBodyElement = filterElementsWithSemanticTextContentOfPage(document);
        Set<String> textSetOfTags = new LinkedHashSet<>(); // эта коллекция не примет дубликаты

        for (Element element : mainContentOfTagBodyElement) {
            recursionDeepIntoElement(element, textSetOfTags);
        }

        StringBuilder textFromSetWithTagSeparatorBuilder = new StringBuilder();
        int sizeTextSet = textSetOfTags.size();
        int counter = 1;
        for (String text : textSetOfTags) {
            if (!text.contains(" ")) {  // уберем одиночные слова текста вытянутые из тега элемента
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
        List<String> tagsToDeleteList = List.of("header", "footer");
        int deleterElementsAfterTagFooter = elements.size(); // для удаления элементов после footer
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).tagName().equals(tagsToDeleteList.get(0))) {
                elements.get(i).remove();
                continue;
            }
            if (elements.get(i).tagName().equals(tagsToDeleteList.get(1))) {
                elements.get(i).remove();
                deleterElementsAfterTagFooter = i;
            }
            if (i > deleterElementsAfterTagFooter) { // элементы после footer
                elements.get(i).remove();
            }
        }
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
        List<String> wordsFromTextList = Arrays.stream(textContentFromPage.split("[^А-Яа-яЁё]"))
                .filter(w -> !w.isBlank() && w.length() != 1)
                .toList();
        Set<String> lemmaWordsFromQuerySet = lemmaDtoSetFromQuery.stream()
                .map(LemmaDto::getLemma)
                .collect(Collectors.toSet());
        LemmaService lemmaService = poolService.getLemmaService();
        Set<String> desiredWordsSet = new HashSet<>();

        for (String word : wordsFromTextList) {
            if (!WordValidator.checkWord(word, lemmaService)) {
                continue;
            }
            String normalBaseFormFromOriginalWord = lemmaService.getNormalBaseFormWord(word);
            for (String lemmaWordFromQuery : lemmaWordsFromQuerySet) {
                if (lemmaWordFromQuery.equals(normalBaseFormFromOriginalWord)) {
                    desiredWordsSet.add(word.toLowerCase());
                }
            }
        }
        return getTextWithSearchWordsBoldSelection(textContentFromPage, desiredWordsSet);
    }

    /**
     * В методе строитель соберет регулярное выражение для поиска нужных слов из набора, чтобы потом вернуть этот текст,
     * в котором эти слова будут обернуты тегом {@code <b>desireWord</b>} для выделения слов на HTML странице.
     * Регулярное выражение соберется в вид: {@code String regex = "(?<!\\p{L})([Лл]ес|[Дд]ом|[Мм]ашина)(?!\\p{L})"}
     * @param textContentFromPage текст контента с пропарсеной страницы
     * @param desiredWords слова которые нужно выделить в тексте
     * @Note <p>{@code "(?<!\\p{L})"} - это граница слова справа ({@code /b} - почему то не работает)</p>
     *       <p>{@code "(?!\\p{L})"} - граница слова слева</p>
     */
    public static String getTextWithSearchWordsBoldSelection(String textContentFromPage, Set<String> desiredWords) {
        StringBuilder builderRegex = new StringBuilder();
        builderRegex.append("(?<!\\p{L})").append("(");

        for (String desiredWord : desiredWords) {
            builderRegex.append(makeWordWithVariableCaseFirstLetter(desiredWord)).append("|");
        }
        int lengthBuilder = builderRegex.length();
        String regex = builderRegex.deleteCharAt(lengthBuilder - 1).append(")").append("(?!\\p{L})").toString();

        return textContentFromPage.replaceAll(regex, "<b>$1</b>");
    }

    private static String makeWordWithVariableCaseFirstLetter(String desiredWord) {
        String firstLetter = desiredWord.substring(0, 1);
        String remainingLettersOfWord = desiredWord.substring(1);
        String firstLetterReady = "[" + firstLetter.toUpperCase() + firstLetter.toLowerCase() + "]";
        return firstLetterReady + remainingLettersOfWord;
    }
}
