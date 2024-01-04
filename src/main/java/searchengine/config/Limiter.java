package searchengine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class Limiter {

   /* protected static int taskCounter = 0;
    private final int coreAmount = Runtime.getRuntime().availableProcessors();

    public synchronized void getFork(HtmlParser task) {
        List<HtmlParser> cashTasks = new ArrayList<>();
        Logger.getLogger(Limiter.class.getName()).info("getFork() + Limiter (" + this + ")");
        while (taskCounter >= coreAmount) {
            taskCounter = 0;
            try {
                wait(60_000);
                Logger.getLogger(HtmlParser.class.getName()).info("wait");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        taskCounter++;
        Logger.getLogger(Limiter.class.getName()).info("taskCounter++  = " + taskCounter);
        cashTasks.add(task);
        notify();
    }*/

}
