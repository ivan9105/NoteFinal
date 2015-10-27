package note.com.notefinal.entity;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Иван on 26.10.2015.
 */
public class Note {
    private UUID id;
    private String title;
    private String description;
    private Date date;
    private Tag tag;

    public Note() {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
