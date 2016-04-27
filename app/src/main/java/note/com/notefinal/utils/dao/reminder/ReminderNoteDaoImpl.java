package note.com.notefinal.utils.dao.reminder;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import note.com.notefinal.entity.Note;
import note.com.notefinal.entity.reminder.ReminderDate;
import note.com.notefinal.entity.reminder.ReminderDay;
import note.com.notefinal.entity.reminder.ReminderHour;
import note.com.notefinal.entity.reminder.ReminderNote;
import note.com.notefinal.entity.reminder.enums.ReminderType;
import note.com.notefinal.utils.CollectionUtil;
import note.com.notefinal.utils.DBUtils;
import note.com.notefinal.utils.DateUtil;
import note.com.notefinal.utils.dao.enums.ReminderView;
import note.com.notefinal.utils.dao.note.NoteDao;
import note.com.notefinal.utils.dao.note.NoteDaoImpl;

/**
 * Created by Иван on 23.04.2016.
 */
public class ReminderNoteDaoImpl implements ReminderDao<ReminderNote> {
    public static final String NOTE_TABLE_NAME = "FINAL_REMINDER_NOTE";
    public static final String NOTE_DATE_TABLE_NAME = "FINAL_REMINDER_DATE";
    public static final String NOTE_HOUR_TABLE_NAME = "FINAL_REMINDER_HOUR";
    public static final String NOTE_DAY_TABLE_NAME = "FINAL_REMINDER_DAY";
    private SQLiteDatabase db;
    private NoteDao<Note> noteDao;

    public ReminderNoteDaoImpl() {
        this.db = DBUtils.getDb();
        this.noteDao = new NoteDaoImpl();
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

                    if (!CollectionUtil.isEmpty(day.getHours())) {
                        for (ReminderHour hour : day.getHours()) {
                            ids_.add(hour.getId().toString());
                        }
                    }
                }
                db.delete(NOTE_DAY_TABLE_NAME, "ID = ?", (String[]) ids.toArray());
                if (!CollectionUtil.isEmpty(ids_)) {
                    db.delete(NOTE_HOUR_TABLE_NAME, "ID = ?", (String[]) ids_.toArray());
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public List<ReminderNote> getItems() {
        return getItems(ReminderView.FULL);
    }

    @Nullable
    @Override
    public ReminderNote getItem(UUID id) {
        return getItem(ReminderView.FULL, id);
    }

    @Override
    public List<ReminderNote> getItems(ReminderView view) {
        List<ReminderNote> res = new ArrayList<>();

        db.beginTransaction();
        try {
            Cursor cursor = db.query(NOTE_TABLE_NAME, new String[]{"ID", "NOTE_ID", "LOOP",
                    "TYPE"}, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String id = cursor.getString(cursor.getColumnIndex("ID"));
                    String noteId = cursor.getString(cursor.getColumnIndex("NOTE_ID"));
                    Note note = noteDao.getItem(UUID.fromString(noteId));
                    String type = cursor.getString(cursor.getColumnIndex("TYPE"));
                    Integer loop = cursor.getInt(cursor.getColumnIndex("LOOP"));
                    ReminderNote item = createNote(UUID.fromString(id), note, type, loop);
                    fillDetails(view, UUID.fromString(id), item);

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

    @Override
    public List<ReminderNote> getItemsByNoteId(ReminderView view, UUID noteId) {
        List<ReminderNote> res = new ArrayList<>();
        db.beginTransaction();
        try {
            Cursor cursor = db.query(NOTE_TABLE_NAME, new String[]{"ID", "NOTE_ID", "LOOP",
                    "TYPE"}, "NOTE_ID = ?", new String[]{noteId.toString()}, null, null, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String id = cursor.getString(cursor.getColumnIndex("ID"));
                    Note note = noteDao.getItem(noteId);
                    String type = cursor.getString(cursor.getColumnIndex("TYPE"));
                    Integer loop = cursor.getInt(cursor.getColumnIndex("LOOP"));
                    ReminderNote item = createNote(UUID.fromString(id), note, type, loop);
                    fillDetails(view, item.getId(), item);

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

    @Override
    public ReminderNote getItem(ReminderView view, UUID id) {
        ReminderNote item = null;
        db.beginTransaction();
        try {
            Cursor cursor = db.query(NOTE_TABLE_NAME, new String[]{"ID", "NOTE_ID", "LOOP",
                    "TYPE"}, "ID = ?", new String[]{id.toString()}, null, null, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String noteId = cursor.getString(cursor.getColumnIndex("NOTE_ID"));
                    Note note = noteDao.getItem(UUID.fromString(noteId));
                    String type = cursor.getString(cursor.getColumnIndex("TYPE"));
                    Integer loop = cursor.getInt(cursor.getColumnIndex("LOOP"));
                    item = createNote(id, note, type, loop);
                    cursor.moveToNext();
                }
            }
            cursor.close();

            fillDetails(view, id, item);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return item;
    }

    private void fillDetails(ReminderView view, UUID id, ReminderNote item) {
        if (item != null) {
            if (view == ReminderView.DATE) {
                findDates(id, item);
            } else if (view == ReminderView.DAYS) {
                findDays(id, item);
            } else if (view == ReminderView.FULL) {
                findDays(id, item);
                findDates(id, item);
            }
        }
    }

    private void findDays(UUID id, ReminderNote item) {
        if (CollectionUtil.isEmpty(item.getDays())) {
            item.setDays(new ArrayList<ReminderDay>());
        }

        Cursor cursor_ = db.query(NOTE_DAY_TABLE_NAME, new String[]{"ID", "NOTE_ID",
                "DAY_"}, "NOTE_ID = ?", new String[]{id.toString()}, null, null, null);
        if (cursor_.moveToFirst()) {
            while (!cursor_.isAfterLast()) {
                String id_ = cursor_.getString(cursor_.getColumnIndex("ID"));
                Date day = DateUtil.toDate(cursor_.getString(cursor_.getColumnIndex("DAY_")));
                ReminderDay day_ = createDay(UUID.fromString(id_), item, day);
                item.getDays().add(day_);
                cursor_.moveToNext();
            }
        }
        cursor_.close();

        for (ReminderDay day_ : item.getDays()) {
            findHours(day_);
        }
    }

    private void findHours(ReminderDay day_) {
        if (!CollectionUtil.isEmpty(day_.getHours())) {
            day_.setHours(new ArrayList<ReminderHour>());
        }

        Cursor cursor__ = db.query(NOTE_HOUR_TABLE_NAME, new String[]{"ID", "DAY_ID",
                "HOUR_"}, "DAY_ID = ?", new String[]{day_.getId().toString()}, null, null, null);

        if (cursor__.moveToFirst()) {
            while (!cursor__.isAfterLast()) {
                String id_ = cursor__.getString(cursor__.getColumnIndex("ID"));
                Date hour = DateUtil.toDate(cursor__.getString(cursor__.getColumnIndex("HOUR_")));
                ReminderHour hour_ = createHour(UUID.fromString(id_), day_, hour);

                day_.getHours().add(hour_);
                cursor__.moveToNext();
            }
        }

        cursor__.close();
    }

    private void findDates(UUID id, ReminderNote item) {
        if (CollectionUtil.isEmpty(item.getDates())) {
            item.setDates(new ArrayList<ReminderDate>());
        }

        Cursor cursor_ = db.query(NOTE_DATE_TABLE_NAME, new String[]{"ID", "NOTE_ID",
                "DATE_"}, "NOTE_ID = ?", new String[]{id.toString()}, null, null, null);
        if (cursor_.moveToFirst()) {
            while (!cursor_.isAfterLast()) {
                String id_ = cursor_.getString(cursor_.getColumnIndex("ID"));
                Date date = DateUtil.toDate(cursor_.getString(cursor_.getColumnIndex("DATE_")));
                ReminderDate date_ = createDate(UUID.fromString(id_), item, date);
                item.getDates().add(date_);
                cursor_.moveToNext();
            }
        }
        cursor_.close();
    }

    private ReminderNote createNote(UUID id, Note note, String type, Integer loop) {
        ReminderNote note_ = new ReminderNote();
        note_.setId(id);
        note_.setNoteId(note.getId());
        note_.setNote(note);
        note_.setType(ReminderType.getById(type));
        note_.setLoop(loop == 1);
        return note_;
    }

    private ReminderDate createDate(UUID id, ReminderNote note, Date date) {
        ReminderDate date_ = new ReminderDate();
        date_.setId(id);
        date_.setNote(note);
        date_.setNoteId(note.getId());
        date_.setDate(date);
        return date_;
    }

    private ReminderDay createDay(UUID id, ReminderNote note, Date day) {
        ReminderDay day_ = new ReminderDay();
        day_.setId(id);
        day_.setNoteId(note.getId());
        day_.setNote(note);
        day_.setDay(day);
        return day_;
    }

    private ReminderHour createHour(UUID id, ReminderDay day, Date hour) {
        ReminderHour hour_ = new ReminderHour();
        hour_.setId(id);
        hour_.setDay(day);
        hour_.setDayId(day.getId());
        hour_.setHour(hour);
        return hour_;
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
