package note.com.notefinal.utils.dao.note;

import android.support.annotation.Nullable;

import java.util.List;

import note.com.notefinal.entity.enums.NotePriority;
import note.com.notefinal.utils.dao.Dao;

/**
 * Created by Иван on 05.01.2016.
 */
public interface NoteDao<Note> extends Dao<Note> {
    List<Note> getItems(String param, @Nullable NotePriority priority);
}
