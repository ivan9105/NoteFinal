package note.com.notefinal.utils;

import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Иван on 28.03.2015.
 */
public class DateUtil {
    public static Date now(){
        return new Date();
    }

    public static String toString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.ENGLISH);
        return sdf.format(date);
    }

    @Nullable
    public static Date toDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.ENGLISH);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date getCurrentDate() {
        long timeInMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return calendar.getTime();
    }

    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    public static Date beginDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }

    public static Date endDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        return calendar.getTime();
    }
}
