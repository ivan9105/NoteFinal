package note.com.notefinal.utils.dao;

import android.support.annotation.Nullable;

import java.util.List;
import java.util.UUID;

import note.com.notefinal.utils.dao.enums.View;

/**
 * Created by Иван on 31.10.2015.
 */
public interface Dao<T> {
    void addItem(T item);
    void updateItem(T item);
    void removeItem(T item);
    List<T> getItems();
    @Nullable
    T getItem(UUID id, View view);
}
