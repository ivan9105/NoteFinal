package note.com.notefinal.utils.dao.reminder;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import note.com.notefinal.entity.reminder.ReminderNote;
import note.com.notefinal.entity.reminder.enums.ReminderType;
import note.com.notefinal.entity.reminder.util.ReminderNoteGSonUtil;
import note.com.notefinal.utils.DBUtils;
import note.com.notefinal.utils.dao.Dao;

/**
 * Created by Иван on 23.04.2016.
 */
public class ReminderNoteDaoImpl implements Dao<ReminderNote> {
    public static final String TABLE_NAME = "FINAL_REMINDER_NOTE";
    private SQLiteDatabase db;

    public ReminderNoteDaoImpl() {
        this.db = DBUtils.getDb();
    }

    @Override
    public void addItem(ReminderNote item) {
        ContentValues cv = getContentValues(item);
        db.beginTransaction();
        try {
            db.insert(TABLE_NAME, null, cv);
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
            db.update(TABLE_NAME, cv, "ID = ?", new String[]{String.valueOf(item.getId())});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void removeItem(ReminderNote item) {
        db.beginTransaction();
        try {
            db.delete(TABLE_NAME, "ID = ?", new String[]{String.valueOf(item.getId())});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public List<ReminderNote> getItems() {
        List<ReminderNote> res = new ArrayList<>();

        db.beginTransaction();
        try {
            Cursor cursor = db.query(TABLE_NAME, new String[]{"ID", "LOOP",
                    "TYPE", "DESCRIPTION", "SETTING_XML"}, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String id = cursor.getString(cursor.getColumnIndex("ID"));
                    String type = cursor.getString(cursor.getColumnIndex("TYPE"));
                    Integer loop = cursor.getInt(cursor.getColumnIndex("LOOP"));
                    String description = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));
                    String settingXml = cursor.getString(cursor.getColumnIndex("SETTING_XML"));
                    ReminderNote item = createNote(UUID.fromString(id), type,
                            loop, description, settingXml);

                    res.add(item);

                    cursor.moveToNext();
                }
            }
            cursor.close();

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return res;
    }

    @Nullable
    @Override
    public ReminderNote getItem(UUID id) {
        ReminderNote item = null;
        db.beginTransaction();
        try {
            Cursor cursor = db.query(TABLE_NAME, new String[]{"ID", "LOOP", "TYPE", "DESCRIPTION",
                    "SETTING_XML"}, "ID = ?", new String[]{id.toString()}, null, null, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String type = cursor.getString(cursor.getColumnIndex("TYPE"));
                    Integer loop = cursor.getInt(cursor.getColumnIndex("LOOP"));
                    String description = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));
                    String settingXml = cursor.getString(cursor.getColumnIndex("SETTING_XML"));
                    item = createNote(id, type, loop, description, settingXml);
                    cursor.moveToNext();
                }
            }
            cursor.close();

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return item;
    }

    private ReminderNote createNote(UUID id, String type, Integer loop,
                                    String description, String settingXml) {
        ReminderNote note = new ReminderNote();
        note.setId(id);
        note.setType(ReminderType.getById(type));
        note.setLoop(loop == 1);
        note.setDescription(description);
        note.setSettingsXml(settingXml);
        ReminderNoteGSonUtil.fromJSON(note);
        return note;
    }

    private ContentValues getContentValues(ReminderNote item) {
        ContentValues cv = new ContentValues();
        cv.put("ID", item.getId().toString());
        cv.put("LOOP", item.getLoop() != null && item.getLoop() ? 1 : 0);
        cv.put("TYPE", item.getType().getId());
        cv.put("DESCRIPTION", item.getDescription());
        cv.put("SETTING_XML", ReminderNoteGSonUtil.toJSON(item));
        return cv;
    }
}
