package note.com.notefinal.utils.dao.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Иван on 01.11.2015.
 */
public enum View {
    LOCAL("S"),
    FULL("F");

    private final String id;

    private static final Map<String, View> data = new HashMap<>();

    static {
        for (View view : values()) {
            data.put(view.id, view);
        }
    }

    View(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static View getById(String id) {
        return data.get(id);
    }
}
