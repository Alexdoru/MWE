package fr.alexdoru.mwe.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class DateUtil {

    private DateUtil() {}

    private static final DateTimeFormatter LOCAL_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm:ss");
    private static final DateTimeFormatter LOCAL_DATE_FORMAT_DAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String localFormatTime(long epoch) {
        return LOCAL_DATE_FORMAT.format(Instant.ofEpochMilli(epoch).atZone(ZoneId.systemDefault()));
    }

    public static String localFormatTimeInDay(long epoch) {
        return LOCAL_DATE_FORMAT_DAY.format(Instant.ofEpochMilli(epoch).atZone(ZoneId.systemDefault()));
    }

    /**
     * Returns the time since input as a string message
     */
    public static String timeSince(long epoch) {
        final long diff = (new Date()).getTime() - epoch;
        if (diff < 1000 * 60) { // less than 60 sec
            return diff / 1000 + "sec";
        } else if (diff < 1000 * 60 * 60) { // less than 60 minutes
            final long sec;
            final long min;
            sec = diff / 1000;
            min = sec / 60;
            return min + "min" + sec % 60 + "sec";
        } else if (diff < 1000 * 60 * 60 * 24) { // less than 24hours
            final long min;
            final long hours;
            min = diff / (1000 * 60);
            hours = min / 60;
            return hours + "h" + min % 60 + "min";
        } else { // more than a day
            final long min;
            final long hours;
            final long days;
            min = diff / (1000 * 60);
            hours = min / 60;
            days = hours / 24;
            return days + (days == 1 ? "day" : "days") + hours % 24 + "h";
        }
    }

    public static String formatTime(int sec) {
        if (sec < 60) {
            return sec + "s";
        }
        final int min = sec / 60;
        final int remainer = sec % 60;
        if (remainer == 0) {
            return min + "m";
        }
        return min + "m " + remainer + "s";
    }

}
