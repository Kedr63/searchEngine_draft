package searchengine.services.utility;

public class TimerExecution {
    public static long startTime;

    public static double computeTimeExecution() {
        long end = System.currentTimeMillis();
        // long start = 0;
        return (double) (end - startTime) / 60_000;
    }
}
