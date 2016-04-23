package note.com.notefinal.utils;

import java.util.Collection;

/**
 * Created by Иван on 23.04.2016.
 */
public class CollectionUtil {
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }
}
