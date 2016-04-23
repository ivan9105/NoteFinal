package note.com.notefinal.utils.dao.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Иван on 01.11.2015.
 */
public enum ReminderView {
    LOCAL("LOCAL"),
    DAYS("DAY"),
    DATE("DATE"),
    FULL("FULL");


    private final String id;

    private static final Map<String, ReminderView> data = new HashMap<>();

    static {
        for (ReminderView view : values()) {
            data.put(view.id, view);
        }
    }

    ReminderView(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static ReminderView getById(String id) {
        return data.get(id);
    }
}
