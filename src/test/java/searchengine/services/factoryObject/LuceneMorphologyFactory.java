package searchengine.services.factoryObject;

import lombok.Data;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;

@Data
public class LuceneMorphologyFactory {

    public static LuceneMorphology getLuceneMorphologyFactory() throws IOException {
        return new RussianLuceneMorphology();
    }
}
