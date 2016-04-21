package note.com.notefinal.entity.reminder;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Иван on 18.04.2016.
 */
public class ReminderHour implements Parcelable {
    private UUID id;
    private UUID dayId;
    private Date hour;

    private ReminderDay day;

    public ReminderHour() {
    }

    protected ReminderHour(Parcel source) {
        String[] data = new String[2];
        source.readStringArray(data);

        this.id = UUID.fromString(data[0]);
        this.dayId = UUID.fromString(data[1]);
        this.hour = new Date(source.readLong());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDayId() {
        return dayId;
    }

    public void setDayId(UUID dayId) {
        this.dayId = dayId;
    }

    public Date getHour() {
        return hour;
    }

    public void setHour(Date hour) {
        this.hour = hour;
    }

    public ReminderDay getDay() {
        return day;
    }

    public void setDay(ReminderDay day) {
        this.day = day;
    }

    public static final Creator<ReminderHour> CREATOR = new Creator<ReminderHour>() {
        @Override
        public ReminderHour createFromParcel(Parcel source) {
            return new ReminderHour(source);
        }

        @Override
        public ReminderHour[] newArray(int size) {
            return new ReminderHour[size];
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
                String.valueOf(dayId)});
        dest.writeLong(hour.getTime());
    }
}
