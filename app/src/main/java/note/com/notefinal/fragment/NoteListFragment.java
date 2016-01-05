package note.com.notefinal.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import note.com.notefinal.MainActivity;
import note.com.notefinal.R;
import note.com.notefinal.adapters.NoteListAdapter;
import note.com.notefinal.entity.Note;
import note.com.notefinal.utils.dao.note.NoteDaoImpl;

/**
 * Created by Иван on 27.10.2015.
 */
public class NoteListFragment extends ListFragment {
    public static final String NAME = "noteList";

    private NoteDaoImpl noteDao;
    private MainActivity mainActivity;

    public NoteListFragment() {
        noteDao = new NoteDaoImpl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_portrait, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final List<Note> data = noteDao.getItems();
        NoteListAdapter adapter = new NoteListAdapter(
                getActivity().getApplicationContext(), data);
        setListAdapter(adapter);

        ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = data.get(position);
                Bundle bundle = new Bundle();
                bundle.putParcelable("item", note);
                mainActivity.initNoteEditor(bundle);
            }
        });

        setPosition(listView);
    }

    private void setPosition(ListView listView) {
        if (getArguments() != null) {
            int position = getArguments().getInt("position");
            listView.setSelection(position);
        }
    }

    public int getCurrentPosition() {
        return getListView().getFirstVisiblePosition();
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void filterItems(@Nullable String param) {
        List<Note> notes;
        if (param == null || param.equals("")) {
            notes = noteDao.getItems();
        } else {
            notes = noteDao.getItems(param);
        }

        NoteListAdapter adapter = (NoteListAdapter) getListAdapter();
        adapter.clear();
        adapter.addAll(notes);
        adapter.notifyDataSetChanged();
    }
}
