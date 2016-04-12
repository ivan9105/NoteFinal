package note.com.notefinal.utils.dao.note;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import note.com.notefinal.entity.Note;
import note.com.notefinal.entity.enums.NotePriority;
import note.com.notefinal.utils.DBUtils;
import note.com.notefinal.utils.DateUtil;
import note.com.notefinal.utils.dao.Dao;
import note.com.notefinal.utils.dao.enums.View;

/**
 * Created by Иван on 01.11.2015.
 */
public class NoteDaoImpl implements NoteDao<Note> {
    public static final String TABLE_NAME = "FINAL_NOTE";
    private SQLiteDatabase db;

    public NoteDaoImpl() {
        this.db = DBUtils.getDb();
    }

    @Override
    public void addItem(Note item) {
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
    public void updateItem(Note item) {
        ContentValues cv = getContentValues(item);

        db.beginTransaction();
        try {
            db.update(TABLE_NAME, cv, "ID = ?", new String[] {String.valueOf(item.getId())});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void removeItem(Note item) {
        db.beginTransaction();
        try {
            db.delete(TABLE_NAME, "ID = ?", new String[] {String.valueOf(item.getId())});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public List<Note> getItems() {
        List<Note> items = new ArrayList<>();

        db.beginTransaction();
        try {
            Cursor cursor = db.query(TABLE_NAME, new String[]{"ID", "TITLE", "DESCRIPTION",
                    "CREATE_TS", "PRIORITY", "EVENT_ID"}, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String id = cursor.getString(cursor.getColumnIndex("ID"));
                    String title = cursor.getString(cursor.getColumnIndex("TITLE"));
                    String description = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));
                    Date createTs = DateUtil.toDate(cursor.getString(cursor.getColumnIndex("CREATE_TS")));
                    String priority = cursor.getString(cursor.getColumnIndex("PRIORITY"));
                    String eventId = cursor.getString(cursor.getColumnIndex("EVENT_ID"));

                    Note note = new Note();
                    note.setId(UUID.fromString(id));
                    note.setTitle(title);
                    note.setDescription(description);
                    note.setCreateTs(createTs);
                    note.setPriority(NotePriority.getById(priority));
                    note.setEventId(eventId);

                    items.add(note);

                    cursor.moveToNext();
                }
            }

            cursor.close();

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return items;
    }

    @Nullable
    @Override
    public Note getItem(UUID id, View view) {
        Note item = null;

        db.beginTransaction();
        try {
            Cursor cursor = db.query(TABLE_NAME, new String[]{"ID", "TITLE", "DESCRIPTION",
                    "CREATE_TS", "PRIORITY", "EVENT_ID"}, "ID = ?", new String[] {id.toString()}, null, null, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String title = cursor.getString(cursor.getColumnIndex("TITLE"));
                    String description = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));
                    Date createTs = DateUtil.toDate(cursor.getString(cursor.getColumnIndex("CREATE_TS")));
                    String priority = cursor.getString(cursor.getColumnIndex("PRIORITY"));
                    String eventId = cursor.getString(cursor.getColumnIndex("EVENT_ID"));

                    Note note = new Note();
                    note.setId(id);
                    note.setTitle(title);
                    note.setDescription(description);
                    note.setCreateTs(createTs);
                    note.setPriority(NotePriority.getById(priority));
                    note.setEventId(eventId);

                    item = note;

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

    private ContentValues getContentValues(Note item) {
        ContentValues cv = new ContentValues();
        cv.put("ID", item.getId().toString());
        cv.put("TITLE", item.getTitle());
        cv.put("DESCRIPTION", item.getDescription());
        cv.put("CREATE_TS", DateUtil.toString(item.getCreateTs()));
        cv.put("PRIORITY", item.getPriority().getId());
        cv.put("EVENT_ID", item.getEventId() != null ? item.getEventId().toString() : null);
        return cv;
    }

    @Override
    public List<Note> getItems(@Nullable String param, @Nullable NotePriority priority) {
        List<Note> items = new ArrayList<>();

        db.beginTransaction();
        try {
            String where;
            String[] params;
            if (priority == null) {
                where = "TITLE LIKE ? OR DESCRIPTION LIKE ?";
                params = new String[]{"%" + param + "%", "%" + param + "%"};
            } else if (param == null || param.equals("")) {
                where = "PRIORITY = ?";
                params = new String[]{priority.getId()};
            } else {
                where = "(TITLE LIKE ? OR DESCRIPTION LIKE ?) AND PRIORITY = ?";
                params = new String[]{"%" + param + "%", "%" + param + "%", priority.getId()};
            }

            Cursor cursor = db.query(TABLE_NAME, new String[]{"ID", "TITLE", "DESCRIPTION",
                    "CREATE_TS", "PRIORITY", "EVENT_ID"}, where,
                    params, null, null, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String id = cursor.getString(cursor.getColumnIndex("ID"));
                    String title = cursor.getString(cursor.getColumnIndex("TITLE"));
                    String description = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));
                    Date createTs = DateUtil.toDate(cursor.getString(cursor.getColumnIndex("CREATE_TS")));
                    String priorityId = cursor.getString(cursor.getColumnIndex("PRIORITY"));
                    String eventId = cursor.getString(cursor.getColumnIndex("EVENT_ID"));

                    Note note = new Note();
                    note.setId(UUID.fromString(id));
                    note.setTitle(title);
                    note.setDescription(description);
                    note.setCreateTs(createTs);
                    note.setPriority(NotePriority.getById(priorityId));
                    note.setEventId(eventId);

                    items.add(note);

                    cursor.moveToNext();
                }
            }

            cursor.close();

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return items;
    }
}
