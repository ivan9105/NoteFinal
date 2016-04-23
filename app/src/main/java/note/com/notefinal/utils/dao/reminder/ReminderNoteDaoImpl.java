package note.com.notefinal.utils.dao.reminder;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

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

    }

    @Override
    public void removeItem(ReminderNote item) {

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
