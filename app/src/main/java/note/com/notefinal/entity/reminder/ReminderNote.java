package note.com.notefinal.entity.reminder;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.UUID;

import note.com.notefinal.entity.Note;
import note.com.notefinal.entity.reminder.enums.ReminderType;

/**
 * Created by Иван on 18.04.2016.
 */
public class ReminderNote implements Parcelable {
    private UUID id;
    private UUID noteId;
    private Boolean loop;
    private String type;

    private List<ReminderDate> dates;
    private List<ReminderDay> days;

    private Note note;

    public ReminderNote() {
    }

    public ReminderNote(Parcel source) {
        String[] data = new String[3];
        source.readStringArray(data);

        boolean[] booleans = new boolean[1];
        source.readBooleanArray(booleans);

        this.id = UUID.fromString(data[0]);
        this.noteId = UUID.fromString(data[1]);
        this.type = data[2];
        this.loop = booleans[0];
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getNoteId() {
        return noteId;
    }

    public void setNoteId(UUID noteId) {
        this.noteId = noteId;
    }

    public Boolean getLoop() {
        return loop;
    }

    public void setLoop(Boolean loop) {
        this.loop = loop;
    }

    public ReminderType getType() {
        return ReminderType.getById(type);
    }

    public void setType(ReminderType type) {
        this.type = type.getId();
    }

    public List<ReminderDate> getDates() {
        return dates;
    }

    public void setDates(List<ReminderDate> dates) {
        this.dates = dates;
    }

    public List<ReminderDay> getDays() {
        return days;
    }

    public void setDays(List<ReminderDay> days) {
        this.days = days;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public static final Creator<ReminderNote> CREATOR = new Creator<ReminderNote>() {
        @Override
        public ReminderNote createFromParcel(Parcel source) {
            return new ReminderNote(source);
        }

        @Override
        public ReminderNote[] newArray(int size) {
            return new ReminderNote[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] data = new String[3];
        data[0] = id.toString();
        data[1] = noteId.toString();
        data[2] = type;

        boolean[] booleans = new boolean[1];
        booleans[0] = loop;

        dest.writeStringArray(data);
        dest.writeBooleanArray(booleans);
    }
}
