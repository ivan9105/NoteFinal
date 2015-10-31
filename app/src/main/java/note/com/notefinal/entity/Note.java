package note.com.notefinal.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Иван on 26.10.2015.
 */
public class Note implements Parcelable {
    private UUID id;
    private String title;
    private String description;
    private Date createTs;
    private Tag tag;

    public Note() {
    }

    public Note(Parcel source) {
        String[] data = new String[5];
        source.readStringArray(data);

        this.tag = new Tag();
        tag.setId(UUID.fromString(data[3]));
        tag.setName(data[4]);

        this.id = UUID.fromString(data[0]);
        this.title = data[1];
        this.description = data[2];
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

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
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
                String.valueOf(tag.getId()),
                tag.getName()
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
