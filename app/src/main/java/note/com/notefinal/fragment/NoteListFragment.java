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

/**
 * Created by Иван on 27.10.2015.
 */
public class NoteListFragment extends ListFragment {
    public static final String NAME = "noteList";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int currentOrientation = getCurrentOrientation();

        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            return inflater.inflate(R.layout.list_portrait, null);
        } else {
            return inflater.inflate(R.layout.list_landscape, null);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initAdapter();
        setCurrentPosition();
    }

    private void setCurrentPosition() {
        int currentPosition = getArguments().getInt("currentPosition");
        getListView().setSelection(currentPosition);
    }

    private void initAdapter() {
        List<Note> data = getArguments().getParcelableArrayList("data");
        NoteListAdapter adapter = new NoteListAdapter(
                getActivity().getApplicationContext(), data, getCurrentOrientation());
        setListAdapter(adapter);
    }

    private int getCurrentOrientation() {
        return getResources().getConfiguration().orientation;
    }
}
