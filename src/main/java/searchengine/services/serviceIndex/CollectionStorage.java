package searchengine.services.serviceIndex;

import lombok.Getter;

import java.awt.geom.IllegalPathStateException;
import java.util.HashSet;
import java.util.Set;

public class CollectionStorage {

   // @Getter
   // protected static Set<String> setString = new HashSet<>();
  //  @Getter
    protected static Set<String> setTitle = new HashSet<>();

    protected static void checkingAndAddToSetTitle(String titleString) {
        if (!setTitle.contains(titleString)) {
            setTitle.add(titleString);
        } else throw new IllegalPathStateException("Такой путь уже не допустим - пропустить");
    }
}
