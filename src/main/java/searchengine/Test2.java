package searchengine;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.List;

public class Test2 {

    public static void main(String[] args) throws IOException {

        LuceneMorphology luceneMorphology = new RussianLuceneMorphology();
        String word = "нет".trim();
        System.out.println("начальное слово: " + word);
        System.out.println();

        List<String> wordNormalForms =
                luceneMorphology.getNormalForms(word);
        wordNormalForms.forEach(System.out::println);
        System.out.println("++++++");

        List<String> wordMorphInfo =
                luceneMorphology.getMorphInfo(word);
        wordMorphInfo.forEach(System.out::println);
        System.out.println("====");

        String baseFormWord = getBaseFormFromNormalForms(word, wordNormalForms);
        System.out.println("baseFormWord = " + baseFormWord);

        System.out.println("----");
        System.out.println("----");

        String wordBase =
                luceneMorphology.getNormalForms("косой").get(0);
        System.out.println("wordBase = " + wordBase);

        System.out.println("_____________________");

        List<String> wordBaseForms1 =
                luceneMorphology.getNormalForms("ровный");
        wordBaseForms1.forEach(System.out::println);

        boolean wordFormsInfoCheck =
                luceneMorphology.checkString("фы");

        System.out.println(wordFormsInfoCheck);

        List<String> wordFormsInfo =
                luceneMorphology.getMorphInfo("прайс");

        List<String> wordFormsInfo1 =
                luceneMorphology.getMorphInfo("ус");

        List<String> wordFormsInfo2 =
                luceneMorphology.getMorphInfo("над");

        List<String> wordFormsInfo3 =
                luceneMorphology.getMorphInfo("ты");

        List<String> wordFormsInfo4 =
                luceneMorphology.getMorphInfo("ровно");

        wordFormsInfo.forEach(System.out::println);
        System.out.println("+======");

        wordFormsInfo1.forEach(System.out::println);
        System.out.println("__________________");

        wordFormsInfo2.forEach(System.out::println);
        System.out.println("__________________");

        wordFormsInfo3.forEach(System.out::println);
        System.out.println(" 4 __________________");

        wordFormsInfo4.forEach(System.out::println);
        System.out.println(" __________________");

        String page = "https://www.svetlovka.ru/events/master-klassy/";
        String regex = "(https://[^,\\s/]+)([^,\\s]+)";
        String domainPartOfAddressUrl = page.replaceAll(regex, "$1");
        String otherPartOfAddressUrl = page.replaceAll(regex, "$2");


        System.out.println(domainPartOfAddressUrl);
        System.out.println(otherPartOfAddressUrl);


    }

    private static String getBaseFormFromNormalForms(String word, List<String> wordNormalForms) {
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
    }


}
