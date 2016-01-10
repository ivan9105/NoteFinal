package note.com.notefinal.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import note.com.notefinal.R;

/**
 * Created by Иван on 09.01.2016.
 */
public class NoteWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        SharedPreferences sp = context.getSharedPreferences(
                ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
        for (int id : appWidgetIds) {
            updateWidget(context, appWidgetManager, sp, id);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        // delete Preferences
        SharedPreferences.Editor editor = context.getSharedPreferences(
                ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE).edit();
        for (int widgetID : appWidgetIds) {
            editor.remove(ConfigActivity.WIDGET_TITLE + widgetID);
        }
        editor.apply();
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager,
                                    SharedPreferences sp, int widgetID) {
        // read from preferences
        String widgetTitle = sp.getString(ConfigActivity.WIDGET_TITLE + widgetID, null);
        if (widgetTitle == null) {
            return;
        }

        if (widgetTitle.length() > 33) {
            widgetTitle = widgetTitle.substring(0, 33) + "...";
        }

        // set values
        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget);
        widgetView.setTextViewText(R.id.title, widgetTitle);

        // update widget
        appWidgetManager.updateAppWidget(widgetID, widgetView);
    }
}
