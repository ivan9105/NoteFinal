package note.com.notefinal;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import note.com.notefinal.fragment.NoteEditorFragment;
import note.com.notefinal.fragment.NoteListFragment;
import note.com.notefinal.utils.AppConfig;
import note.com.notefinal.utils.DBUtils;
import note.com.notefinal.utils.LogUtils;


public class MainActivity extends ActionBarActivity {
    private NoteListFragment listFragment;
    private NoteEditorFragment noteEditorFragment;
    private String currentFragment;
    private int position;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //check db version if version is changed than run update db task and progress bar
        updateDbIfNeeded();
        //init main fragment
        initListFragment(savedInstanceState);
    }

    public void initListFragment(Bundle savedInstanceState) {
        LogUtils.log(MainActivity.class, "init list fragment");
        listFragment = new NoteListFragment();
        currentFragment = NoteListFragment.NAME;

        listFragment.setArguments(savedInstanceState);

        getSupportFragmentManager().beginTransaction().replace(R.id.content, listFragment,
                NoteListFragment.NAME).commit();

        if (menu != null) {
            menu.findItem(R.id.addItem).setVisible(true);
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
                Toast.makeText(this, "Add item", Toast.LENGTH_SHORT)
                        .show();
                initNoteEditor(null);
                menu.findItem(R.id.addItem).setVisible(false);
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


}
