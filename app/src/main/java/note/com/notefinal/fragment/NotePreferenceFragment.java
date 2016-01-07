package note.com.notefinal.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import note.com.notefinal.MainActivity;
import note.com.notefinal.R;

/**
 * Created by Иван on 07.01.2016.
 */
public class NotePreferenceFragment extends PreferenceFragment {
    public static final String NAME = "notePreferences";

    private MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void close() {
        mainActivity.initListFragment(null);
    }
}
