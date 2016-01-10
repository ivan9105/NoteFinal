package note.com.notefinal.widget;

import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import note.com.notefinal.R;
import note.com.notefinal.adapters.NoteListAdapter;
import note.com.notefinal.entity.Note;
import note.com.notefinal.utils.AppConfig;
import note.com.notefinal.utils.DBUtils;
import note.com.notefinal.utils.dao.note.NoteDaoImpl;

/**
 * Created by Иван on 09.01.2016.
 */
public class ConfigActivity extends ListActivity {
    int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    Intent resultValue;

    public final static String WIDGET_PREF = "widget_pref";
    public final static String WIDGET_TITLE = "widget_title_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get widget id
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        //is correct
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        // set result
        resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);

        // negative result
        setResult(RESULT_CANCELED, resultValue);

        setContentView(R.layout.config);

        // init db
        int dbVersion = AppConfig.getDbVersion(getApplicationContext());
        DBUtils dbHelper = new DBUtils(this, dbVersion);
        //init static db
        SQLiteDatabase db = DBUtils.getDb();
        dbHelper.onUpgrade(db, db.getVersion(), dbVersion);

        NoteDaoImpl noteDao = new NoteDaoImpl();
        List<Note> data = noteDao.getItems();
        NoteListAdapter adapter = new NoteListAdapter(getApplicationContext(), data);
        setListAdapter(adapter);

        ListView listView = getListView();
        listView.setOnItemClickListener(new ItemClickListener(data, this));
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        private List<Note> data;
        private ConfigActivity activity;

        public ItemClickListener(List<Note> data, ConfigActivity activity) {
            this.data = data;
            this.activity = activity;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Note note = data.get(position);

            //write data to preference
            SharedPreferences sp = getSharedPreferences(WIDGET_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(WIDGET_TITLE + widgetID, note.getTitle());
            editor.apply();

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(activity);
            NoteWidget.updateWidget(activity, appWidgetManager, sp, widgetID);

            // positive result
            setResult(RESULT_OK, resultValue);

            finish();
        }
    }
}
