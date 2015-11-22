package note.com.notefinal.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import note.com.notefinal.R;
import note.com.notefinal.adapters.NoteListAdapter;
import note.com.notefinal.entity.Note;
import note.com.notefinal.utils.dao.note.NoteDaoImpl;

/**
 * Created by Иван on 22.11.2015.
 */
public class NoteRemoveListFragment extends ListFragment {
    public static final String NAME = "noteRemoveList";

    private NoteDaoImpl noteDao;

    List<Note> removed = new ArrayList<>();
    private Integer backgroundColor;

    public NoteRemoveListFragment() {
        noteDao = new NoteDaoImpl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.removed_list, container, false);

        if (backgroundColor == null) {
            backgroundColor = getBackgroundColor(view);
        }

        return view;
    }

    private int getBackgroundColor(View view) {
        return ((ColorDrawable) view.getBackground()).getColor();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initAdapter();
    }

    private void initAdapter() {
        List<Note> data = noteDao.getItems(note.com.notefinal.utils.dao.enums.View.FULL);
        NoteListAdapter adapter = new NoteListAdapter(
                getActivity().getApplicationContext(), data, getCurrentOrientation());
        setListAdapter(adapter);
    }

    private int getCurrentOrientation() {
        return getResources().getConfiguration().orientation;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (getBackgroundColor(v) == Color.GRAY) {
            v.setBackgroundColor(backgroundColor);
        } else {
            v.setBackgroundColor(Color.GRAY);
        }
        Note selected = (Note) getListAdapter().getItem(position);
        super.onListItemClick(l, v, position, id);
    }
}
