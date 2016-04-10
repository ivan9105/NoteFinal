package note.com.notefinal.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import note.com.notefinal.MainActivity;
import note.com.notefinal.R;

/**
 * Created by Иван on 10.04.2016.
 */
public class NoteCalendarFragment extends Fragment {
    public static final String NAME = "noteCalendar";
    private MainActivity mainActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note_calendar, container, false);
        TextView status = (TextView) view.findViewById(R.id.status);
        return view;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void commit() {
        mainActivity.initListFragment(null);
    }
}
