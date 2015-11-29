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

import java.util.List;

import note.com.notefinal.MainActivity;
import note.com.notefinal.R;
import note.com.notefinal.adapters.NoteRemoveListAdapter;
import note.com.notefinal.entity.Note;
import note.com.notefinal.utils.dao.note.NoteDaoImpl;

/**
 * Created by Иван on 22.11.2015.
 */
public class NoteRemoveListFragment extends ListFragment {
    public static final String NAME = "noteRemoveList";

    private MainActivity mainActivity;
    private NoteDaoImpl noteDao;

    public NoteRemoveListFragment() {
        noteDao = new NoteDaoImpl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.removed_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final List<Note> data = noteDao.getItems(note.com.notefinal.utils.dao.enums.View.FULL);
        final NoteRemoveListAdapter adapter = new NoteRemoveListAdapter(
                getActivity().getApplicationContext(), data, getCurrentOrientation());
        setListAdapter(adapter);

        ListView list = getListView();

        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note item = data.get(position);
                if (adapter.getSelected().contains(item)) {
                    adapter.getSelected().remove(item);
                } else {
                    adapter.getSelected().add(item);
                }
                adapter.notifyDataSetChanged();
            }
        });

        View view = getView();

        if (view != null) {
            Button ok = (Button) view.findViewById(R.id.ok);
            Button selectAll = (Button) view.findViewById(R.id.selectAll);

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (Note note : adapter.getSelected()) {
                        noteDao.removeItem(note);
                    }
                    mainActivity.initListFragment(null);
                }
            });

            selectAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (Note note : data) {
                        if (!adapter.getSelected().contains(note)) {
                            adapter.getSelected().add(note);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    private int getCurrentOrientation() {
        return getResources().getConfiguration().orientation;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
}
