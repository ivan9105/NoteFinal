package note.com.notefinal.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * Created by Иван on 26.10.2015.
 */
public class Tag implements Parcelable{
    private UUID id;
    private String name;

    public Tag() {
    }

    public Tag(Parcel source) {
        String[] data = new String[2];
        source.writeStringArray(data);

        this.id = UUID.fromString(data[0]);
        this.name = data[1];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                String.valueOf(id),
                name
        });
    }

    public static final Parcelable.Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel source) {
            return new Tag(source);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[0];
        }
    };
}
