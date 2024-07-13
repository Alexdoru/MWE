package fr.alexdoru.mwe.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    private static final SimpleDateFormat LOCAL_DATE_FORMAT;
    private static final SimpleDateFormat LOCAL_DATE_FORMAT_DAY;
    private static final SimpleDateFormat EST_DATE_FORMAT;

    static {
        LOCAL_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss");
        LOCAL_DATE_FORMAT_DAY = new SimpleDateFormat("dd/MM/yyyy");
        EST_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss 'EST'");
        EST_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("America/New_York"));
    }

    public static String localFormatTime(long epoch) {
        return LOCAL_DATE_FORMAT.format(epoch);
    }

    public static String localFormatTimeInDay(long epoch) {
        return LOCAL_DATE_FORMAT_DAY.format(epoch);
    }

    public static String ESTFormatTime(long epoch) {
        return EST_DATE_FORMAT.format(epoch);
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

}
