package note.com.notefinal.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Иван on 31.10.2015.
 */
public class LogUtils {
    private enum Level {
        INFO("i"),
        DEBUG("d"),
        VERBOSE("v"),
        WARN("w"),
        ERROR("e");

        private final String id;

        private static final Map<String, Level> data = new HashMap<>();

        static {
            for (Level level : values()) {
                data.put(level.id, level);
            }
        }

        Level(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public static Level getById(String id) {
            return data.get(id);
        }
    }

    public static void log(Class cls, String msg) {
        Log.i(cls.getName(), msg);
    }

    public static void log(String tag, String msg) {
        Log.i(tag, msg);
    }

    public static void log(Level level, Class cls, String msg) {
        if (level == Level.INFO) {
            Log.i(cls.getName(), msg);
        } else if (level == Level.DEBUG) {
            Log.d(cls.getName(), msg);
        } else if (level == Level.VERBOSE) {
            Log.v(cls.getName(), msg);
        } else if (level == Level.WARN) {
            Log.w(cls.getName(), msg);
        } else if (level == Level.ERROR) {
            Log.e(cls.getName(), msg);
        }
    }

    public static void log(Level level, String tag, String msg) {
        if (level == Level.INFO) {
            Log.i(tag, msg);
        } else if (level == Level.DEBUG) {
            Log.d(tag, msg);
        } else if (level == Level.VERBOSE) {
            Log.v(tag, msg);
        } else if (level == Level.WARN) {
            Log.w(tag, msg);
        } else if (level == Level.ERROR) {
            Log.e(tag, msg);
        }
    }
}
