package note.com.notefinal.entity.reminder;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import note.com.notefinal.entity.Note;
import note.com.notefinal.entity.reminder.enums.ReminderType;

/**
 * Created by Иван on 18.04.2016.
 */
public class ReminderNote implements Parcelable {
    private UUID id;
    private Boolean loop;
    private String type;
    private String description;
    private String settingsXml;

    private List<ReminderDate> dates;
    private List<ReminderDay> days;

    private Note note;

    public ReminderNote() {
    }

    public ReminderNote(Parcel source) {
        String[] data = new String[4];
        source.readStringArray(data);

        boolean[] booleans = new boolean[1];
        source.readBooleanArray(booleans);

        this.id = UUID.fromString(data[0]);
        this.type = data[1];
        this.description = data[2];
        this.settingsXml = data[3];
        this.loop = booleans[0];
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSettingsXml() {
        return settingsXml;
    }

    public void setSettingsXml(String settingsXml) {
        this.settingsXml = settingsXml;
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
        String[] data = new String[4];
        data[0] = id.toString();
        data[1] = type;
        data[2] = description;
        data[3] = settingsXml;

        boolean[] booleans = new boolean[1];
        booleans[0] = loop;

        dest.writeStringArray(data);
        dest.writeBooleanArray(booleans);
    }

    public class ReminderDate {
        private UUID id;
        private Date date;

        public ReminderDate() {
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }

    public class ReminderDay {
        private UUID id;
        private Date day;
        private List<ReminderHour> hours;

        public ReminderDay() {
        }

        public UUID getId() {
            return id;
        }

        public Date getDay() {
            return day;
        }

        public List<ReminderHour> getHours() {
            return hours;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public void setDay(Date day) {
            this.day = day;
        }

        public void setHours(List<ReminderHour> hours) {
            this.hours = hours;
        }
    }

    public class ReminderHour {
        private UUID id;
        private Date hour;

        public ReminderHour() {
        }

        public Date getHour() {
            return hour;
        }

        public void setHour(Date hour) {
            this.hour = hour;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }
    }
}
