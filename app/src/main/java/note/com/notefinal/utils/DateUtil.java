package note.com.notefinal.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Иван on 28.03.2015.
 */
public class DateUtil {
    public static Date now(){
        return new Date();
    }

    public static String convertToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss", Locale.ENGLISH);
        return sdf.format(date);
    }
}
