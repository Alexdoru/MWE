package fr.alexdoru.mwe.utils;

public class TimerUtil {

    private final long period;
    private long lastUpdate;

    /**
     * @param period is in milliseconds
     */
    public TimerUtil(long period) {
        this.period = period;
    }

    public boolean update() {
        final long time = System.currentTimeMillis();
        if (time - lastUpdate > period) {
            lastUpdate = time;
            return true;
        }
        return false;
    }

    public boolean canUpdate() {
        return System.currentTimeMillis() - lastUpdate > period;
    }

}
