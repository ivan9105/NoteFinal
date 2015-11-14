package note.com.notefinal.utils.dao.tag;

import android.support.annotation.Nullable;

import note.com.notefinal.entity.Tag;
import note.com.notefinal.utils.dao.Dao;
import note.com.notefinal.utils.dao.enums.View;

/**
 * Created by Иван on 14.11.2015.
 */
public interface TagDao<T> extends Dao<T> {
    @Nullable
    T findByName(String name, View view);
}
