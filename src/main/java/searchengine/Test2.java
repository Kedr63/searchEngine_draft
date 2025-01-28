package searchengine;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.List;

public class Test2 {

    
    public static void main(String[] args) throws IOException {

        LuceneMorphology luceneMorphology = new RussianLuceneMorphology();

        List<String> wordBaseForms =
                luceneMorphology.getNormalForms("авто");
        wordBaseForms.forEach(System.out::println);

        boolean wordFormsInfoCheck =
                luceneMorphology.checkString("фы");

        System.out.println(wordFormsInfoCheck);

        List<String> wordFormsInfo =
                luceneMorphology.getMorphInfo("прайс");

        wordFormsInfo.forEach(System.out::println);

        System.out.println("__________________");

        String page = "https://www.svetlovka.ru/events/master-klassy/";
        String regex = "(https://[^,\\s/]+)([^,\\s]+)";
        String domainPartOfAddressUrl = page.replaceAll(regex, "$1");
        String otherPartOfAddressUrl = page.replaceAll(regex, "$2");


        System.out.println(domainPartOfAddressUrl);
        System.out.println(otherPartOfAddressUrl);
        
        
        
    }
    
    
}
