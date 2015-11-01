package note.com.notefinal.utils.dao;

import android.support.annotation.Nullable;

import java.util.List;
import java.util.Map;

import note.com.notefinal.utils.dao.enums.View;

/**
 * Created by Иван on 31.10.2015.
 */
public interface Dao<T> {
    public void addItem(T item);
    public void updateItem(T item);
    public void removeItem(T item);
    public List<T> getItems(View view);
    public boolean buyItem(T item);
}
