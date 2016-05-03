package note.com.notefinal.fragment.reminder;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import note.com.notefinal.R;
import note.com.notefinal.activities.ReminderActivity;
import note.com.notefinal.adapters.reminder.ReminderListAdapter;
import note.com.notefinal.entity.reminder.ReminderNote;
import note.com.notefinal.utils.dao.reminder.ReminderNoteDaoImpl;

/**
 * Created by Иван on 02.05.2016.
 */
public class ReminderListFragment extends ListFragment {
    public static final String NAME = "reminderList";

    private ReminderNoteDaoImpl reminderDao;
    private ReminderActivity reminderActivity;

    public ReminderListFragment() {
        reminderDao = new ReminderNoteDaoImpl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reminder_list_portrait, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final List<ReminderNote> data = reminderDao.getItems();
        ReminderListAdapter adapter = new ReminderListAdapter(getActivity(), data);
        setListAdapter(adapter);

        ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReminderNote note = data.get(position);
                Bundle bundle = new Bundle();
                bundle.putParcelable("item", note);
                //
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

    public void setReminderActivity(ReminderActivity reminderActivity) {
        this.reminderActivity = reminderActivity;
    }
}
