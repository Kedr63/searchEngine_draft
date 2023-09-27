package searchengine.dto.indexing;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
public class ThreadStopper {
    public static boolean stopper = false;


}
