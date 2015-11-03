package note.com.notefinal.utils.dao.note;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import note.com.notefinal.entity.Tag;
import note.com.notefinal.utils.dao.Dao;
import note.com.notefinal.utils.dao.enums.View;

/**
 * Created by Иван on 02.11.2015.
 */
public class TagDao implements Dao<Tag> {
    public static final String TABLE_NAME = "FINAL_TAG";
    private SQLiteDatabase db;

    public TagDao(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void addItem(Tag item) {
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
    public void updateItem(Tag item) {
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
    public void removeItem(Tag item) {
        db.beginTransaction();
        try {
            db.delete(TABLE_NAME, "id = ?", new String[] {String.valueOf(item.getId())});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public List<Tag> getItems(View view) {
        List<Tag> items = new ArrayList<>();

        db.beginTransaction();
        try {
            Cursor cursor = db.query(TABLE_NAME, new String[]{"ID", "NAME"},
                    null, null, null, null, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String id = cursor.getString(cursor.getColumnIndex("ID"));
                    String name = cursor.getString(cursor.getColumnIndex("NAME"));

                    Tag tag = new Tag();
                    tag.setId(UUID.fromString(id));
                    tag.setName(name);
                    items.add(tag);
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
    public Tag getItem(UUID id, View view) {
        Tag item = null;

        db.beginTransaction();
        try {
            Cursor cursor = db.query(TABLE_NAME, new String[]{"ID", "NAME"},
                    "ID = ?", new String[] {id.toString()}, null, null, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String name = cursor.getString(cursor.getColumnIndex("NAME"));

                    Tag tag = new Tag();
                    tag.setId(id);
                    tag.setName(name);

                    item = tag;

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

    private ContentValues getContentValues(Tag item) {
        ContentValues cv = new ContentValues();
        cv.put("ID", item.getId().toString());
        cv.put("NAME", item.getName());
        return cv;
    }
}
