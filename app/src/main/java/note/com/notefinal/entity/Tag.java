package note.com.notefinal.entity;

import java.util.UUID;

/**
 * Created by Иван on 26.10.2015.
 */
public class Tag {
    private UUID id;
    private String name;

    public Tag() {
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
}
