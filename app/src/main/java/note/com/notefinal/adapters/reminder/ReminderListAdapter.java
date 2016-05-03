package note.com.notefinal.adapters.reminder;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import note.com.notefinal.R;
import note.com.notefinal.entity.reminder.ReminderNote;
import note.com.notefinal.utils.CollectionUtil;
import note.com.notefinal.utils.DateUtil;

/**
 * Created by Иван on 02.05.2016.
 */
public class ReminderListAdapter extends ArrayAdapter<ReminderNote> {
    public int MAX_TITLE_LENGTH = 60;
    private Context ctx;
    private List<ReminderNote> data;

    public ReminderListAdapter(Context ctx, List<ReminderNote> data) {
        super(ctx, R.layout.reminder_portrait, data);

        this.ctx = ctx;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.reminder_portrait, parent, false);

        LinearLayout mainLayout = (LinearLayout) view.findViewById(R.id.mainLayout);
        Pair<Integer, Integer> screenResolution = getScreenResolution();
        if (screenResolution.second > 1000) {
            MAX_TITLE_LENGTH = 73;
            int minHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 95,
                    getContext().getResources().getDisplayMetrics());
            mainLayout.setMinimumHeight(minHeight);
        }

        ReminderNote note = data.get(position);
        String title = note.getDescription();
        if (title.length() > MAX_TITLE_LENGTH) {
            title = String.format("%s%s", title.substring(0, MAX_TITLE_LENGTH - 3), "...");
        }

        String info = getInfo(note);

        ((TextView) view.findViewById(R.id.titleField)).setText(title);
        TextView infoField = (TextView) view.findViewById(R.id.infoField);
        infoField.setText(info);

        return view;
    }

    @Nullable
    private String getInfo(ReminderNote note) {
        String info = null;
        if (note.getLoop() != null && note.getLoop()) {
            if (!CollectionUtil.isEmpty(note.getDays())) {
                Map<Integer, List<ReminderNote.ReminderHour>> hours = new HashMap<>();

                for (ReminderNote.ReminderDay day : note.getDays()) {
                    hours.put(day.getDay(), day.getHours());
                }

                Date now = DateUtil.now();

                Pair<Date, Long> positiveDate = new Pair<>(null, null);
                Pair<Date, Long> negativeDate = new Pair<>(null, null);

                for (Map.Entry<Integer, List<ReminderNote.ReminderHour>> entry : hours.entrySet()) {
                    Calendar diff = Calendar.getInstance();
                    diff.setTime(now);

                    for (ReminderNote.ReminderHour hour_ : entry.getValue()) {
                        Calendar diff_ = Calendar.getInstance();
                        diff_.setTime(hour_.getHour());
                        diff_.set(Calendar.YEAR, diff.get(Calendar.YEAR));
                        diff_.set(Calendar.DAY_OF_YEAR, diff.get(Calendar.DAY_OF_YEAR));
                        diff_.set(Calendar.DAY_OF_WEEK, entry.getKey());

                        long difference = diff_.getTime().getTime() - diff.getTime().getTime();
                        if (difference >= 0 &&
                                (positiveDate.second == null || positiveDate.second > difference)) {
                            positiveDate = new Pair<>(diff_.getTime(), difference);
                        } else if (negativeDate.second == null || negativeDate.second < difference) {
                            negativeDate = new Pair<>(diff_.getTime(), difference);
                        }
                    }
                }

                if (negativeDate.first != null) {
                    info = DateUtil.toString(DateUtil.addDays(negativeDate.first, 7));
                } else if (positiveDate.first != null) {
                    info = DateUtil.toString(positiveDate.first);
                }
            }
        } else {
            if (!CollectionUtil.isEmpty(note.getDates())) {
                Date now = DateUtil.now();

                Pair<Date, Long> positiveDate = new Pair<>(null, null);
                Pair<Date, Long> negativeDate = new Pair<>(null, null);

                for (ReminderNote.ReminderDate date : note.getDates()) {
                    long diff = now.getTime() - date.getDate().getTime();
                    if (diff >= 0 && (positiveDate.second == null || positiveDate.second > diff)) {
                        positiveDate = new Pair<>(date.getDate(), diff);
                    } else if (negativeDate.second == null || negativeDate.second < diff) {
                        negativeDate = new Pair<>(date.getDate(), diff);
                    }
                }

                if (negativeDate.first != null) {
                    info = DateUtil.toString(negativeDate.first);
                } else if (positiveDate.first != null) {
                    info = DateUtil.toString(positiveDate.first);
                }
            }
        }
        return info;
    }

    private Pair<Integer, Integer> getScreenResolution() {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        return new Pair<>(width, height);
    }
}
