package note.com.notefinal.entity.reminder;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Иван on 18.04.2016.
 */
public class ReminderDay implements Parcelable {
    private UUID id;
    private UUID noteId;
    private Date day;

    private List<ReminderHour> hours;

    private ReminderNote note;

    public ReminderDay() {
    }

    public ReminderDay(Parcel source) {
        String[] data = new String[2];
        source.readStringArray(data);

        this.id = UUID.fromString(data[0]);
        this.noteId = UUID.fromString(data[1]);
        this.day = new Date(source.readLong());
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

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public List<ReminderHour> getHours() {
        return hours;
    }

    public void setHours(List<ReminderHour> hours) {
        this.hours = hours;
    }

    public ReminderNote getNote() {
        return note;
    }

    public void setNote(ReminderNote note) {
        this.note = note;
    }

    public static final Creator<ReminderDay> CREATOR = new Creator<ReminderDay>() {
        @Override
        public ReminderDay createFromParcel(Parcel source) {
            return new ReminderDay(source);
        }

        @Override
        public ReminderDay[] newArray(int size) {
            return new ReminderDay[size];
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
                String.valueOf(noteId)
        });
        dest.writeLong(day.getTime());
    }
}
