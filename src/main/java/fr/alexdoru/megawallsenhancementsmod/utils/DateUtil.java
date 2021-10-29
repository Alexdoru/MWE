package fr.alexdoru.megawallsenhancementsmod.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    public static String localformatTimestamp(long epoch) {

        SimpleDateFormat Df = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss 'local'");
        //SimpleDateFormat Df = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
        //Calendar currentTime = Calendar.getInstance();
        return Df.format(epoch);

    }

    public static String ESTformatTimestamp(long epoch) {

        SimpleDateFormat ESTDf = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss 'EST'");
        //SimpleDateFormat ESTDf = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
        TimeZone etTimeZone = TimeZone.getTimeZone("America/New_York");
        ESTDf.setTimeZone(etTimeZone);
        //Calendar currentTime = Calendar.getInstance();
        return ESTDf.format(epoch);

    }

    public static String localformatTimestampday(long epoch) {

        SimpleDateFormat Df = new SimpleDateFormat("dd/MM/yyyy");
        return Df.format(epoch);

    }

    /**
     * Returns the time since input as a string message
     */
    public static String timeSince(long epoch) {

        long diff = (new Date()).getTime() - epoch;
        String msg;

        if (diff < 1000 * 60) { // less than 60 sec

            return diff / 1000 + "sec";

        } else if (diff < 1000 * 60 * 60) { // less than 60 minutes

            long sec;
            long min;

            sec = diff / 1000;
            min = sec / 60;

            return min + "min" + sec % 60 + "sec";


        } else if (diff < 1000 * 60 * 60 * 24) { // less than 24hours

            long min;
            long hours;

            min = diff / (1000 * 60);
            hours = min / 60;

            return hours + "h" + min % 60 + "min";

        } else { // more than a day

            long min;
            long hours;
            long days;

            min = diff / (1000 * 60);
            hours = min / 60;
            days = hours / 24;

            return days + (days == 1 ? "day" : "days") + hours % 24 + "h";
            //return String.valueOf(days) + (days==1?"day":"days") + String.valueOf(hours%24) + "h" + String.valueOf(min%60) + "min";

        }

    }

}
