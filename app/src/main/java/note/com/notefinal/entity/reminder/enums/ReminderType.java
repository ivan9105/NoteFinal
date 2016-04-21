package note.com.notefinal.entity.reminder.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Иван on 19.04.2016.
 */
public enum ReminderType {
    RINGTONE("R"),
    VIBRATION("V");

    private final String id;
    private static final Map<String, ReminderType> data = new HashMap<>();

    static {
        for (ReminderType type : values()) {
            data.put(type.getId(), type);
        }
    }

    ReminderType(String id) { this.id = id; }

    public String getId() {
        return id;
    }

    public static ReminderType getById(String id) {
        return data.get(id);
    }
}
