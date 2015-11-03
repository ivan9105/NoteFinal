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
import note.com.notefinal.entity.Tag;
import note.com.notefinal.utils.DateUtil;
import note.com.notefinal.utils.dao.Dao;
import note.com.notefinal.utils.dao.enums.View;

/**
 * Created by Иван on 01.11.2015.
 */
public class NoteDao implements Dao<Note> {
    public static final String TABLE_NAME = "FINAL_NOTE";
    private SQLiteDatabase db;
    private TagDao tagDao;

    public NoteDao(SQLiteDatabase db) {
        this.db = db;
        this.tagDao = new TagDao(db);
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
            db.delete(TABLE_NAME, "id = ?", new String[] {String.valueOf(item.getId())});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public List<Note> getItems(View view) {
        List<Note> items = new ArrayList<>();

        db.beginTransaction();
        try {
            Cursor cursor = db.query(TABLE_NAME, new String[]{"ID", "TITLE", "DESCRIPTION",
                    "CREATE_TS", "TAG_ID"}, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String id = cursor.getString(cursor.getColumnIndex("ID"));
                    String title = cursor.getString(cursor.getColumnIndex("TITLE"));
                    String description = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));
                    Date createTs = DateUtil.toDate(cursor.getString(cursor.getColumnIndex("CREATE_TS")));

                    Note note = new Note();
                    note.setId(UUID.fromString(id));
                    note.setTitle(title);
                    note.setDescription(description);
                    note.setCreateTs(createTs);

                    if (view == View.FULL) {
                        String tagId = cursor.getString(cursor.getColumnIndex("TAG_ID"));
                        Tag tag = tagDao.getItem(UUID.fromString(tagId), view);
                        note.setTag(tag);
                    }

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
                    "CREATE_TS", "TAG_ID"}, "ID = ?", new String[] {id.toString()}, null, null, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String title = cursor.getString(cursor.getColumnIndex("TITLE"));
                    String description = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));
                    Date createTs = DateUtil.toDate(cursor.getString(cursor.getColumnIndex("CREATE_TS")));

                    Note note = new Note();
                    note.setId(id);
                    note.setTitle(title);
                    note.setDescription(description);
                    note.setCreateTs(createTs);

                    if (view == View.FULL) {
                        String tagId = cursor.getString(cursor.getColumnIndex("TAG_ID"));
                        Tag tag = tagDao.getItem(UUID.fromString(tagId), view);
                        note.setTag(tag);
                    }

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
        cv.put("TAG_ID", item.getId().toString());
        return cv;
    }
}
