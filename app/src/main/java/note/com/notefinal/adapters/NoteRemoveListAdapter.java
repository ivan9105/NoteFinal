package note.com.notefinal.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import note.com.notefinal.R;
import note.com.notefinal.entity.Note;

/**
 * Created by Иван on 28.11.2015.
 */
public class NoteRemoveListAdapter extends ArrayAdapter<Note> {
    private Context ctx;
    private List<Note> data;
    private int currentOrientation;
    private List<Note> selected = new ArrayList<>();

    public NoteRemoveListAdapter(Context ctx, List<Note> data, int currentOrientation) {
        super(ctx, R.layout.note_portrait, data);
        this.ctx = ctx;
        this.data = data;
        this.currentOrientation = currentOrientation;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.note_portrait, parent, false);
        Note note = data.get(position);

        String title = note.getTitle();
        String tag = (note.getTag() != null ? note.getTag().getName() : "");
        String formattedDate = getFormattedDate(note.getCreateTs());
        String[] dateArr = formattedDate.split(" ");

        ((TextView) view.findViewById(R.id.titleField)).setText(title);
        ((TextView) view.findViewById(R.id.tagField)).setText(tag);
        ((TextView) view.findViewById(R.id.dateField)).setText(dateArr[0]);
        ((TextView) view.findViewById(R.id.timeField)).setText(dateArr[1]);

        if (selected.contains(note)) {
            view.setBackgroundColor(Color.parseColor("#D5F3F9"));
        }

        return view;
    }

    public List<Note> getSelected() {
        return selected;
    }

    private String getFormattedDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy hh:mm", Locale.ENGLISH);
        return sdf.format(date);
    }
}
