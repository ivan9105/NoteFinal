package note.com.notefinal.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.UUID;

import note.com.notefinal.entity.enums.NotePriority;

/**
 * Created by Иван on 26.10.2015.
 */
public class Note implements Parcelable {
    private UUID id;
    private String title;
    private String description;
    private Date createTs;
    private String priority;

    public Note() {
    }

    public Note(Parcel source) {
        String[] data = new String[5];
        source.readStringArray(data);

        this.id = UUID.fromString(data[0]);
        this.title = data[1];
        this.description = data[2];
        this.priority = data[3];
        this.createTs = new Date(source.readLong());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTs() {
        return createTs;
    }

    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    public NotePriority getPriority() {
        return NotePriority.getById(priority);
    }

    public void setPriority(NotePriority priority) {
        this.priority = priority.getId();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                String.valueOf(id),
                title,
                description,
                priority
        });
        dest.writeLong(createTs.getTime());
    }

    public static final Parcelable.Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[0];
        }
    };
}
