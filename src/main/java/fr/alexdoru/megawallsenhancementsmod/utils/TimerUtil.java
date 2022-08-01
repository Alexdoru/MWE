package fr.alexdoru.megawallsenhancementsmod.utils;

public class TimerUtil {

    private final long period;
    private long lastUpdate = 0;

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

}
