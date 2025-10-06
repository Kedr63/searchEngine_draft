package searchengine.services.utility;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TextContentFromPageParser {

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

    private static void recursionDeepIntoElement(Element element, Set<String> setTextOfTags) {
       Elements elements = element.children();
       if (!elements.isEmpty()) {
           for (Element child : elements) {
               recursionDeepIntoElement(child, setTextOfTags);
           }
       } else {
           if (!element.text().isEmpty()) {
               setTextOfTags.add(element.text());
           }
       }
    }


}
