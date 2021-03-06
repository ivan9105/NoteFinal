package note.com.notefinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

import note.com.notefinal.activities.ReminderActivity;
import note.com.notefinal.fragment.NoteCalendarFragment;
import note.com.notefinal.fragment.NoteEditorFragment;
import note.com.notefinal.fragment.NoteListFragment;
import note.com.notefinal.fragment.NotePreferenceFragment;
import note.com.notefinal.fragment.NoteRemoveListFragment;
import note.com.notefinal.utils.AppConfig;
import note.com.notefinal.utils.DBUtils;
import note.com.notefinal.utils.LogUtils;


public class MainActivity extends AppCompatActivity {
    private NoteListFragment listFragment;
    private NoteRemoveListFragment noteRemoveListFragment;
    private NoteEditorFragment noteEditorFragment;
    private NotePreferenceFragment preferenceFragment;
    private NoteCalendarFragment calendarFragment;
    private String currentFragment;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //check db version if version is changed than run update db task and progress bar
        updateDbIfNeeded();
        //init main fragment
        initListFragment(savedInstanceState);

        initActionBar();
    }

    private void initActionBar() {
        makeActionOverflowMenuShown();

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle("");
        supportActionBar.setDisplayShowHomeEnabled(true);
        supportActionBar.setLogo(R.mipmap.icon);
        supportActionBar.setDisplayUseLogoEnabled(true);
        setToolBarColor(supportActionBar);
    }

    private void setToolBarColor(ActionBar supportActionBar) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            int[] attrs = {R.attr.colorPrimary};
            TypedArray ta = obtainStyledAttributes(R.style.AppTheme, attrs);
            String text = ta.getString(0);
            if (text != null) {
                ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor(text));
                colorDrawable.setAlpha(190);
                supportActionBar.setBackgroundDrawable(colorDrawable);
            }
            ta.recycle();
        }
    }

    public void initListFragment(Bundle savedInstanceState) {
        LogUtils.log(MainActivity.class, "init list fragment");
        listFragment = new NoteListFragment();

        if (currentFragment != null && NotePreferenceFragment.NAME.equals(currentFragment)) {
            getFragmentManager().beginTransaction().remove(preferenceFragment).commit();
        }

        currentFragment = NoteListFragment.NAME;

        listFragment.setArguments(savedInstanceState);
        listFragment.setMainActivity(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.content, listFragment,
                NoteListFragment.NAME).commit();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (menu != null) {
            menu.findItem(R.id.addItem).setVisible(true);
            menu.findItem(R.id.removeItems).setVisible(true);
            menu.findItem(R.id.menu_search).setVisible(true);
            menu.findItem(R.id.settings).setVisible(true);
            menu.findItem(R.id.calendar).setVisible(true);

            if (!preferences.getBoolean("searchEnable", true)) {
                menu.findItem(R.id.menu_search).setVisible(false);
            }
        }
    }

    public void initNoteEditor(Bundle savedInstanceState) {
        LogUtils.log(MainActivity.class, "init note editor");
        noteEditorFragment = new NoteEditorFragment();
        currentFragment = NoteEditorFragment.NAME;

        noteEditorFragment.setArguments(savedInstanceState);
        noteEditorFragment.setMainActivity(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.content, noteEditorFragment,
                NoteEditorFragment.NAME).commit();

        menu.findItem(R.id.addItem).setVisible(false);
        menu.findItem(R.id.removeItems).setVisible(false);
        menu.findItem(R.id.menu_search).setVisible(false);
        menu.findItem(R.id.settings).setVisible(false);
    }

    public void initRemovedNoteList(Bundle savedInstanceState) {
        LogUtils.log(MainActivity.class, "init note removed items fragment");
        noteRemoveListFragment = new NoteRemoveListFragment();
        currentFragment = NoteRemoveListFragment.NAME;

        noteRemoveListFragment.setArguments(savedInstanceState);
        noteRemoveListFragment.setMainActivity(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.content, noteRemoveListFragment,
                NoteEditorFragment.NAME).commit();

        menu.findItem(R.id.addItem).setVisible(false);
        menu.findItem(R.id.removeItems).setVisible(false);
        menu.findItem(R.id.settings).setVisible(false);
    }

    private void initPreferences() {
        preferenceFragment = new NotePreferenceFragment();
        currentFragment = NotePreferenceFragment.NAME;
        preferenceFragment.setMainActivity(this);

        getSupportFragmentManager().beginTransaction().remove(listFragment).commit();

        getFragmentManager().beginTransaction().replace(R.id.content, preferenceFragment,
                NotePreferenceFragment.NAME).commit();

        menu.findItem(R.id.addItem).setVisible(false);
        menu.findItem(R.id.removeItems).setVisible(false);
        menu.findItem(R.id.menu_search).setVisible(false);
        menu.findItem(R.id.settings).setVisible(false);
    }

    private void initCalendar() {
        calendarFragment = new NoteCalendarFragment();
        currentFragment = NoteCalendarFragment.NAME;
        calendarFragment.setMainActivity(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.content, calendarFragment,
                NoteEditorFragment.NAME).commit();

        menu.findItem(R.id.addItem).setVisible(false);
        menu.findItem(R.id.removeItems).setVisible(false);
        menu.findItem(R.id.menu_search).setVisible(false);
        menu.findItem(R.id.settings).setVisible(false);
        menu.findItem(R.id.calendar).setVisible(false);
    }

    private void updateDbIfNeeded() {
        LogUtils.log(MainActivity.class, "update db");
        int dbVersion = AppConfig.getDbVersion(getApplicationContext());
        DBUtils dbHelper = new DBUtils(this, dbVersion);
        //init static db
        SQLiteDatabase db = DBUtils.getDb();
        dbHelper.onUpgrade(db, db.getVersion(), dbVersion);
    }

    @Override
    public void onBackPressed() {
        if (currentFragment.equals(NoteEditorFragment.NAME)) {
            noteEditorFragment.commit();
        } else if (currentFragment.equals(NotePreferenceFragment.NAME)) {
            preferenceFragment.close();
        } else if (currentFragment.equals(NoteCalendarFragment.NAME)) {
            calendarFragment.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actions_menu, menu);
        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addItem:
                initNoteEditor(null);
                break;
            case R.id.removeItems:
                initRemovedNoteList(null);
                break;
            case R.id.settings:
                initPreferences();
                menu.findItem(R.id.settings).setVisible(false);
                break;
            case R.id.addReminder:
                Intent intent = new Intent(this, ReminderActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_search:
                if ((NoteListFragment.NAME.equals(currentFragment))) {
                    if (!listFragment.isShowSearch()) {
                        item.setVisible(false);
                        listFragment.showSearch(true);
                    }
                } else if (NoteRemoveListFragment.NAME.equals(currentFragment)) {
                    if (!noteRemoveListFragment.isShowSearch()) {
                        item.setVisible(false);
                        noteRemoveListFragment.showSearch(true);
                    }
                }
                break;
            case R.id.calendar:
                initCalendar();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (NoteListFragment.NAME.equals(currentFragment)) {
            outState.putInt("position", listFragment.getCurrentPosition());
        }
        super.onSaveInstanceState(outState);
    }

    private void makeActionOverflowMenuShown() {
        //devices with hardware menu button (e.g. Samsung Note) don't show action overflow menu
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            LogUtils.log(this.getClass(), e.getLocalizedMessage());
        }
    }

    public void showSearch() {
        MenuItem item = menu.findItem(R.id.menu_search);
        item.setVisible(true);
    }
}
