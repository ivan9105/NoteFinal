package note.com.notefinal.utils.dao.reminder;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import note.com.notefinal.entity.reminder.ReminderDate;
import note.com.notefinal.entity.reminder.ReminderDay;
import note.com.notefinal.entity.reminder.ReminderHour;
import note.com.notefinal.entity.reminder.ReminderNote;
import note.com.notefinal.utils.CollectionUtil;
import note.com.notefinal.utils.DBUtils;
import note.com.notefinal.utils.DateUtil;
import note.com.notefinal.utils.dao.enums.ReminderView;

/**
 * Created by Иван on 23.04.2016.
 */
public class ReminderNoteDaoImpl implements ReminderDao<ReminderNote> {
    public static final String NOTE_TABLE_NAME = "FINAL_REMINDER_NOTE";
    public static final String NOTE_DATE_TABLE_NAME = "FINAL_REMINDER_DATE";
    public static final String NOTE_HOUR_TABLE_NAME = "FINAL_REMINDER_HOUR";
    public static final String NOTE_DAY_TABLE_NAME = "FINAL_REMINDER_DAY";
    private SQLiteDatabase db;

    public ReminderNoteDaoImpl() {
        this.db = DBUtils.getDb();
    }

    @Override
    public void addItem(ReminderNote item) {
        ContentValues cv = getContentValues(item);
        db.beginTransaction();
        try {
            db.insert(NOTE_TABLE_NAME, null, cv);

            if (!CollectionUtil.isEmpty(item.getDates())) {
                for (ReminderDate date : item.getDates()) {
                    db.insert(NOTE_DATE_TABLE_NAME, null, getContentValues(date));
                }
            }

            if (!CollectionUtil.isEmpty(item.getDays())) {
                for (ReminderDay day : item.getDays()) {
                    db.insert(NOTE_DAY_TABLE_NAME, null, getContentValues(day));

                    if (!CollectionUtil.isEmpty(day.getHours())) {
                        for (ReminderHour hour : day.getHours()) {
                            db.insert(NOTE_HOUR_TABLE_NAME, null, getContentValues(hour));
                        }
                    }
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void updateItem(ReminderNote item) {
        ContentValues cv = getContentValues(item);

        db.beginTransaction();
        try {
            db.update(NOTE_TABLE_NAME, cv, "ID = ?", new String[]{String.valueOf(item.getId())});

            updateDates(item);
            updateDays(item);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void updateDays(ReminderNote item) {
        if (!CollectionUtil.isEmpty(item.getDays())) {
            List<String> ids = new ArrayList<>();
            for (ReminderDay day : item.getDays()) {
                ids.add(day.getId().toString());
            }

            Cursor cursor = db.query(NOTE_DAY_TABLE_NAME, new String[]{"ID"},
                    "ID = ?", (String[]) ids.toArray(), null, null, null);
            List<String> exists = new ArrayList<>();
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String id = cursor.getString(cursor.getColumnIndex("ID"));
                    exists.add(id);
                    cursor.moveToNext();
                }
            }
            cursor.close();

            for (ReminderDay day : item.getDays()) {
                if (exists.contains(day.getId().toString())) {
                    db.update(NOTE_DAY_TABLE_NAME, getContentValues(day), "ID = ?",
                            new String[]{String.valueOf(day.getId())});
                } else {
                    db.insert(NOTE_DAY_TABLE_NAME, null, getContentValues(day));
                }
                updateHours(day);
            }
        }
    }

    private void updateHours(ReminderDay day) {
        if (!CollectionUtil.isEmpty(day.getHours())) {
            List<String> ids = new ArrayList<>();
            for (ReminderHour hour : day.getHours()) {
                ids.add(hour.getId().toString());
            }

            Cursor cursor = db.query(NOTE_HOUR_TABLE_NAME, new String[]{"ID"},
                    "ID = ?", (String[]) ids.toArray(), null, null, null);
            List<String> exists = new ArrayList<>();
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String id = cursor.getString(cursor.getColumnIndex("ID"));
                    exists.add(id);
                    cursor.moveToNext();
                }
            }
            cursor.close();

            for (ReminderHour hour : day.getHours()) {
                if (exists.contains(hour.getId().toString())) {
                    db.update(NOTE_HOUR_TABLE_NAME, getContentValues(hour), "ID = ?",
                            new String[]{String.valueOf(day.getId())});
                } else {
                    db.insert(NOTE_HOUR_TABLE_NAME, null, getContentValues(hour));
                }
            }
        }
    }

    private void updateDates(ReminderNote item) {
        if (!CollectionUtil.isEmpty(item.getDates())) {
            List<String> ids = new ArrayList<>();
            for (ReminderDate date : item.getDates()) {
                ids.add(date.getId().toString());
            }

            Cursor cursor = db.query(NOTE_DATE_TABLE_NAME, new String[]{"ID"},
                    "ID = ?", (String[]) ids.toArray(), null, null, null);
            List<String> exists = new ArrayList<>();
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String id = cursor.getString(cursor.getColumnIndex("ID"));
                    exists.add(id);
                    cursor.moveToNext();
                }
            }
            cursor.close();

            for (ReminderDate date : item.getDates()) {
                if (exists.contains(date.getId().toString())) {
                    db.update(NOTE_DATE_TABLE_NAME, getContentValues(date), "ID = ?",
                            new String[]{String.valueOf(date.getId())});
                } else {
                    db.insert(NOTE_DATE_TABLE_NAME, null, getContentValues(date));
                }
            }
        }
    }

    @Override
    public void removeItem(ReminderNote item) {
        db.beginTransaction();
        try {
            db.delete(NOTE_TABLE_NAME, "ID = ?", new String[]{String.valueOf(item.getId())});

            if (!CollectionUtil.isEmpty(item.getDates())) {
                List<String> ids = new ArrayList<>();
                for (ReminderDate date : item.getDates()) {
                    ids.add(date.getId().toString());
                }
                db.delete(NOTE_DATE_TABLE_NAME, "ID = ?", (String[]) ids.toArray());
            }

            if (!CollectionUtil.isEmpty(item.getDays())) {
                List<String> ids = new ArrayList<>();
                List<String> ids_ = new ArrayList<>();
                for (ReminderDay day : item.getDays()) {
                    ids.add(day.getId().toString());

                    for (ReminderHour hour : day.getHours()) {
                        ids_.add(hour.getId().toString());
                    }
                }
                db.delete(NOTE_DAY_TABLE_NAME, "ID = ?", (String[]) ids.toArray());
                db.delete(NOTE_HOUR_TABLE_NAME, "ID = ?", (String[]) ids_.toArray());
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public List<ReminderNote> getItems() {
        return null;
    }

    @Nullable
    @Override
    public ReminderNote getItem(UUID id) {
        return null;
    }

    @Override
    public List<ReminderNote> getItems(ReminderView view) {
        return null;
    }

    @Override
    public List<ReminderNote> getItemsByNoteId(ReminderView view, UUID noteId) {
        return null;
    }

    @Override
    public ReminderNote getItem(ReminderView view) {
        return null;
    }

    private ContentValues getContentValues(ReminderNote item) {
        ContentValues cv = new ContentValues();
        cv.put("ID", item.getId().toString());
        cv.put("NOTE_ID", item.getNoteId().toString());
        cv.put("LOOP", item.getLoop() != null && item.getLoop() ? 1 : 0);
        cv.put("TYPE", item.getType().getId());
        return cv;
    }

    private ContentValues getContentValues(ReminderDate item) {
        ContentValues cv = new ContentValues();
        cv.put("ID", item.getId().toString());
        cv.put("NOTE_ID", item.getNoteId().toString());
        cv.put("DATE_", DateUtil.toString(item.getDate()));
        return cv;
    }

    private ContentValues getContentValues(ReminderDay item) {
        ContentValues cv = new ContentValues();
        cv.put("ID", item.getId().toString());
        cv.put("NOTE_ID", item.getNoteId().toString());
        cv.put("DAY_", DateUtil.toString(item.getDay()));
        return cv;
    }

    private ContentValues getContentValues(ReminderHour item) {
        ContentValues cv = new ContentValues();
        cv.put("ID", item.getId().toString());
        cv.put("DAY_ID", item.getDayId().toString());
        cv.put("HOUR_", DateUtil.toString(item.getHour()));
        return cv;
    }
}
