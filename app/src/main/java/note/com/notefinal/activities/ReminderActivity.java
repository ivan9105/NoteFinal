package note.com.notefinal.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

import note.com.notefinal.MainActivity;
import note.com.notefinal.R;
import note.com.notefinal.fragment.NoteListFragment;
import note.com.notefinal.fragment.reminder.ReminderListFragment;
import note.com.notefinal.utils.LogUtils;

public class ReminderActivity extends AppCompatActivity {
    private ReminderListFragment listFragment;
    private String currentFragment;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder);

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

    public void initListFragment(Bundle savedInstanceState) {
        LogUtils.log(ReminderActivity.class, "init list fragment");

        listFragment = new ReminderListFragment();
        currentFragment = ReminderListFragment.NAME;
        listFragment.setArguments(savedInstanceState);
        listFragment.setReminderActivity(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.content, listFragment,
                NoteListFragment.NAME).commit();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (menu != null) {
            menu.findItem(R.id.addItem).setVisible(false);
            menu.findItem(R.id.removeItems).setVisible(false);
            menu.findItem(R.id.menu_search).setVisible(false);
            menu.findItem(R.id.settings).setVisible(false);
            menu.findItem(R.id.calendar).setVisible(false);
            menu.findItem(R.id.addReminder).setVisible(false);

            if (!preferences.getBoolean("searchEnable", true)) {
                menu.findItem(R.id.menu_search).setVisible(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actions_menu, menu);
        this.menu = menu;

        menu.findItem(R.id.addItem).setVisible(false);
        menu.findItem(R.id.removeItems).setVisible(false);
        menu.findItem(R.id.menu_search).setVisible(false);
        menu.findItem(R.id.settings).setVisible(false);
        menu.findItem(R.id.calendar).setVisible(false);
        menu.findItem(R.id.addReminder).setVisible(false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.getBoolean("searchEnable", true)) {
            menu.findItem(R.id.menu_search).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addItem:
                break;
            case R.id.removeItems:
                break;
            case R.id.settings:
                break;
            case R.id.menu_search:
                break;
            case R.id.calendar:
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
}
