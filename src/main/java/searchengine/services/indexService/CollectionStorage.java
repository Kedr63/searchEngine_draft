package searchengine.services.indexService;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class CollectionStorage {

   // @Getter
   // protected static Set<String> setString = new HashSet<>();
  //  @Getter
      protected static Set<String> setPaths = new HashSet<>();
   // protected static ConcurrentSkipListSet<String> setPaths = new ConcurrentSkipListSet<>();

    protected static boolean checkingAndAddToSetLink(String link) {
       /* if (!setPaths.contains(titleString)) {
            setPaths.add(titleString);
        }*/
        if (!setPaths.contains(link)){
            setPaths.add(link);
            return true;
        }
        Logger.getLogger(CollectionStorage.class.getName()).info("Такой путь уже не допустим - пропустить");
        return false;

       //  else throw new IllegalPathStateException("Такой путь уже не допустим - пропустить");
    }
}
