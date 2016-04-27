package note.com.notefinal.utils.dao.reminder;

import java.util.List;
import java.util.UUID;

import note.com.notefinal.utils.dao.Dao;
import note.com.notefinal.utils.dao.enums.ReminderView;

/**
 * Created by Иван on 21.04.2016.
 */
public interface ReminderDao<ReminderNote> extends Dao<ReminderNote> {
    List<ReminderNote> getItems(ReminderView view);
    List<ReminderNote> getItemsByNoteId(ReminderView view, UUID noteId);
    ReminderNote getItem(ReminderView view, UUID id);
}
