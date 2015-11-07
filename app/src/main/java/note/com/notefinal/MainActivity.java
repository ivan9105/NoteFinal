package note.com.notefinal;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import note.com.notefinal.fragment.NoteListFragment;
import note.com.notefinal.utils.AppConfig;
import note.com.notefinal.utils.DBUtils;
import note.com.notefinal.utils.LogUtils;


public class MainActivity extends ActionBarActivity {
    private FragmentTransaction fragmentTransaction;
    private NoteListFragment listFragment;
    private String currentFragment;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //check db version if version is changed than run update db task and progress bar
        updateDbIfNeeded();
        //init main fragment
        initListFragment(savedInstanceState);
    }

    private void initListFragment(Bundle savedInstanceState) {
        LogUtils.log(MainActivity.class, "init list fragment");
        listFragment = new NoteListFragment();
        currentFragment = NoteListFragment.NAME;

        listFragment.setArguments(savedInstanceState);

        try {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content, listFragment, NoteListFragment.NAME);
        } finally {
            fragmentTransaction.commit();
        }
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (NoteListFragment.NAME.equals(currentFragment)) {
            outState.putInt("position", listFragment.getCurrentPosition());
        }
        super.onSaveInstanceState(outState);
    }


}
