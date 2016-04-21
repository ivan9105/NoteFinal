package note.com.notefinal.entity.reminder;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Иван on 18.04.2016.
 */
public class ReminderDate implements Parcelable {
    private UUID id;
    private UUID noteId;
    private Date date;
    private ReminderNote note;

    protected ReminderDate(Parcel source) {
        String[] data = new String[2];
        source.readStringArray(data);

        this.id = UUID.fromString(data[0]);
        this.noteId = UUID.fromString(data[1]);
        this.date = new Date(source.readLong());
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ReminderNote getNote() {
        return note;
    }

    public void setNote(ReminderNote note) {
        this.note = note;
    }

    public static final Creator<ReminderDate> CREATOR = new Creator<ReminderDate>() {
        @Override
        public ReminderDate createFromParcel(Parcel in) {
            return new ReminderDate(in);
        }

        @Override
        public ReminderDate[] newArray(int size) {
            return new ReminderDate[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                String.valueOf(id),
                String.valueOf(noteId)});
        dest.writeLong(date.getTime());
    }
}
