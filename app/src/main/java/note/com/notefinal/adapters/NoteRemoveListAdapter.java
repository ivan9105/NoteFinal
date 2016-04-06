package note.com.notefinal.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import note.com.notefinal.R;
import note.com.notefinal.entity.Note;
import note.com.notefinal.entity.enums.NotePriority;

/**
 * Created by Иван on 28.11.2015.
 */
public class NoteRemoveListAdapter extends ArrayAdapter<Note> {
    public int MAX_TITLE_LENGTH = 60;

    private Context ctx;
    private List<Note> data;
    private List<Note> selected = new ArrayList<>();

    public NoteRemoveListAdapter(Context ctx, List<Note> data) {
        super(ctx, R.layout.note_portrait, data);
        this.ctx = ctx;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.note_portrait, parent, false);
        LinearLayout mainLayout = (LinearLayout) view.findViewById(R.id.mainLayout);
        Pair<Integer, Integer> screenResolution = getScreenResolution();
        if (screenResolution.second > 1000) {
            MAX_TITLE_LENGTH = 73;
            int minHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 95,
                    getContext().getResources().getDisplayMetrics());
            mainLayout.setMinimumHeight(minHeight);
        }

        Note note = data.get(position);

        String title = note.getTitle();
        if (title.length() > MAX_TITLE_LENGTH) {
            title = String.format("%s%s", title.substring(0, MAX_TITLE_LENGTH - 3), "...");
        }
        String formattedDate = getFormattedDate(note.getCreateTs());

        ((TextView) view.findViewById(R.id.titleField)).setText(title);
        ((TextView) view.findViewById(R.id.dateField)).setText(formattedDate);

        initSquareLayout(view, note);

        if (selected.contains(note)) {
            Drawable background = ctx.getResources().getDrawable(R.drawable.background_selected);

            int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackgroundDrawable(background);
            } else {
                view.setBackground(background);
            }
        }

        return view;
    }

    public List<Note> getSelected() {
        return selected;
    }

    private void initSquareLayout(View view, Note note) {
        TextView squareView = (TextView) view.findViewById(R.id.square_view);

        Drawable background = null;
        if (note.getPriority() == NotePriority.NORMAL) {
            background = getContext().getResources().getDrawable(R.drawable.priority_shape_green);
        } else if (note.getPriority() == NotePriority.MINOR) {
            background = getContext().getResources().getDrawable(R.drawable.priority_shape_lite_green);
        } else if (note.getPriority() == NotePriority.MAJOR) {
            background = getContext().getResources().getDrawable(R.drawable.priority_shape_orange);
        } else if (note.getPriority() == NotePriority.CRITICAL) {
            background = getContext().getResources().getDrawable(R.drawable.priority_shape_red);
        }

        if (background == null) {
            background = getContext().getResources().getDrawable(R.drawable.priority_shape_green);
        }

        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            squareView.setBackgroundDrawable(background);
        } else {
            squareView.setBackground(background);
        }
    }

    private String getFormattedDate(Date date) {
        Locale currentLocale = Locale.getDefault();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM. yyyy HH:mm", currentLocale);
        return sdf.format(date);
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
