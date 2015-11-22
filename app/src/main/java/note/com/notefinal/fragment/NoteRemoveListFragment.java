package note.com.notefinal.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import note.com.notefinal.MainActivity;
import note.com.notefinal.R;
import note.com.notefinal.adapters.NoteListAdapter;
import note.com.notefinal.entity.Note;
import note.com.notefinal.utils.dao.note.NoteDaoImpl;

/**
 * Created by Иван on 22.11.2015.
 */
public class NoteRemoveListFragment extends ListFragment {
    public static final String NAME = "noteRemoveList";

    private MainActivity mainActivity;
    private NoteDaoImpl noteDao;
    private List<Note> removed = new ArrayList<>();

    public NoteRemoveListFragment() {
        noteDao = new NoteDaoImpl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.removed_list, container, false);

        Button ok = (Button) view.findViewById(R.id.ok);
        Button selectAll = (Button) view.findViewById(R.id.selectAll);
        final ListView list = (ListView) view.findViewById(android.R.id.list);

        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                removed.add((Note) list.getAdapter().getItem(position));
            }
        });

        list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Note item = (Note) list.getAdapter().getItem(position);
                removed.remove(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Note note : removed) {
                    //Todo check this
//                    noteDao.removeItem(note);
                }

                mainActivity.initListFragment(null);
            }
        });

        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < list.getAdapter().getCount(); i++) {
                    list.setItemChecked(i, true);
                }
            }
        });

        return view;
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

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
}
