package searchengine;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.List;

public class Test2 {

    public static void main(String[] args) throws IOException {

        LuceneMorphology luceneMorphology = new RussianLuceneMorphology();
        String word = "лежал".trim();
        boolean isWord = luceneMorphology.checkString(word);
        System.out.println("начальное слово: " + word + " isWord: " + isWord);
        System.out.println();

        List<String> wordNormalForms =
                luceneMorphology.getNormalForms(word);
        wordNormalForms.forEach(System.out::println);
        System.out.println("++++++");

        List<String> wordMorphInfo =
                luceneMorphology.getMorphInfo(word);
        wordMorphInfo.forEach(System.out::println);
        System.out.println("====");

        String baseFormWord = getBaseFormFromNormalForms(word, wordMorphInfo);
        System.out.println("baseFormWord = " + baseFormWord);
        String baseFormWord2 = getBaseFormFromNormalForms2(word, wordNormalForms);
        System.out.println("baseFormWord2 = " + baseFormWord2);

        System.out.println("----");
        System.out.println("----");

        String wordBase =
                luceneMorphology.getNormalForms("купить").get(0);
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

        System.out.println("____________________________________");
        String text = "Описание почему нужно покупать автомобиль для путешествий по стране и на море";
        String str = "автомобиль";
        text = text.replaceAll(str, "<b>" + str + "</b>");
        System.out.println("Result replace: " + text);

        System.out.println("____________________________________");
        String text1 = "Описание почему нужно покупать автомобиль для путешествий по стране и на море";
        String str1 = "([а-яА-Я]+){3}";
        text1 = text1.replaceAll(str1, "1");
        System.out.println("Result replace: " + text1);

        StringBuilder builder = new StringBuilder();
        builder.append("sa").append("|").append("za").append("|");
        System.out.println(builder.toString());
        int lengthB = builder.length();
        System.out.println("length builder: " + lengthB);
        System.out.println(builder.deleteCharAt(lengthB - 1).toString());



        //  String regex2 = "([Лл]ес)|([Дд]ом)|([Мм]ашина)";
        String regex2 = "(?<!\\p{L})([Лл]ес|[Дд]ом|[Мм]ашина)(?!\\p{L})";
        String text2 = "Прилесье зимой. В лесу стоял дом. К нему подъехала Машина. Лес очень красивый потому что он лес";
        System.out.println("Замена текста:  " + text2.replaceAll(regex2, "<b>$1</b>"));


//        String regex3 = "(лес)|(дом)|(машина)";
//        String result = Pattern.compile(regex3, Pattern.CASE_INSENSITIVE)
//                .matcher(text2)
//                .replaceAll(Matcher.quoteReplacement("<b>" + "$1$2$3" + "</b>"));
//        System.out.println("С Pattern: " + result);
        System.out.println("----------------");
        char ch = 'Г';
        System.out.println("char " + ch);
        int code = ch;
        System.out.println("code " + code);
        char ch1 = 'г';
        System.out.println("char1 " + ch1);
        int code1 = ch1;
        System.out.println("code1 " + code1);

        char ch3 = 'А';
        System.out.println("char3 " + ch3);
        int code3 = ch3;
        System.out.println("code3 " + code3);
        char ch4 = 'а';
        System.out.println("char4 " + ch4);
        int code4 = ch4;
        System.out.println("code4 " + code4);



    }

    private static String getBaseFormFromNormalForms(String word, List<String> wordForm) {
        String baseFormWord = "";
        for (String wordNormalForm : wordForm) {
            if (wordNormalForm.compareTo(word) == 0) {
                baseFormWord = wordNormalForm;
                break;
            }
        }
        if (baseFormWord.isEmpty()) {
            baseFormWord = wordForm.get(0);
        }
        return baseFormWord;
    }

    private static String getBaseFormFromNormalForms2(String word, List<String> wordMophInfo) {
        String baseFormWord = "";
        for (String wordNormalForm : wordMophInfo) {
            if (wordNormalForm.compareTo(word) == 0) {
                baseFormWord = wordNormalForm;
                break;
            }
        }
        if (baseFormWord.isEmpty()) {
            baseFormWord = wordMophInfo.get(0);
        }
        return baseFormWord;
    }


}
