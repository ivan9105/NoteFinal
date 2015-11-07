package note.com.notefinal.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import note.com.notefinal.R;
import note.com.notefinal.adapters.NoteListAdapter;
import note.com.notefinal.entity.Note;
import note.com.notefinal.utils.dao.note.NoteDao;

/**
 * Created by Иван on 27.10.2015.
 */
public class NoteListFragment extends ListFragment {
    public static final String NAME = "noteList";

    private NoteDao noteDao;

    public NoteListFragment() {
        noteDao = new NoteDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int currentOrientation = getCurrentOrientation();

        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            return inflater.inflate(R.layout.list_portrait, container, false);
        } else {
            return inflater.inflate(R.layout.list_landscape, container, false);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initAdapter();
        setPosition();
    }

    private void setPosition() {
        if (getArguments() != null) {
            int position = getArguments().getInt("position");
            getListView().setSelection(position);
        }
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

    public int getCurrentPosition() {
        return getListView().getFirstVisiblePosition();
    }
}
