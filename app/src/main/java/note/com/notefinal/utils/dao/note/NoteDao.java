package note.com.notefinal.utils.dao.note;

import java.util.List;

import note.com.notefinal.entity.Note;
import note.com.notefinal.utils.dao.Dao;

/**
 * Created by Иван on 05.01.2016.
 */
public interface NoteDao<Note> extends Dao<Note> {
    public List<Note> getItems(String param);
}
