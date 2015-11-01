package note.com.notefinal.utils.dao.note;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.List;

import note.com.notefinal.entity.Note;
import note.com.notefinal.utils.DateUtil;
import note.com.notefinal.utils.dao.Dao;
import note.com.notefinal.utils.dao.enums.View;

/**
 * Created by Иван on 01.11.2015.
 */
public class NoteDao implements Dao<Note> {
    private SQLiteDatabase db;

    @Override
    public void addItem(Note item) {

    }

    @Override
    public void updateItem(Note item) {

    }

    @Override
    public void removeItem(Note item) {

    }

    @Override
    public List<Note> getItems(View view) {
        return null;
    }

    @Override
    public boolean buyItem(Note item) {
        return false;
    }

    private ContentValues getContentValues(Note item) {
        ContentValues cv = new ContentValues();
        cv.put("ID", item.getId().toString());
        cv.put("TITLE", item.getTitle());
        cv.put("DESCRIPTION", item.getDescription());
        cv.put("CREATE_TS", DateUtil.convertToString(item.getCreateTs()));
        cv.put("TAG_ID", item.getId().toString());
        return cv;
    }
}
