package searchengine;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.List;

public class Test2 {

    public static void main(String[] args) throws IOException {

        LuceneMorphology luceneMorphology = new RussianLuceneMorphology();
        List<String> wordBaseForms =
                luceneMorphology.getNormalForms("м");
        wordBaseForms.forEach(System.out::println);


        List<String> wordFormsInfo =
                luceneMorphology.getMorphInfo("м");

        wordFormsInfo.forEach(System.out::println);
    }
}
