package note.com.notefinal.entity.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Иван on 05.04.2016.
 */
public enum NotePriority {
    MINOR("minor"),
    NORMAL("normal"),
    MAJOR("major"),
    CRITICAL("critical");

    private final String id;
    private static final Map<String, NotePriority> data = new HashMap<>();

    static {
        for (NotePriority priority : values()) {
            data.put(priority.getId(), priority);
        }
    }

    NotePriority(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static NotePriority getById(String id) {
        return data.get(id);
    }
}
