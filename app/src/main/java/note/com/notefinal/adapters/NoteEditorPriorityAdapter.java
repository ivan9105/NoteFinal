package note.com.notefinal.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import note.com.notefinal.MainActivity;
import note.com.notefinal.R;
import note.com.notefinal.entity.enums.NotePriority;

/**
 * Created by Иван on 05.04.2016.
 */
public class NoteEditorPriorityAdapter extends ArrayAdapter<Pair<String, NotePriority>> {
    private List<Pair<String, NotePriority>> data;
    private LayoutInflater inflater;

    public NoteEditorPriorityAdapter(MainActivity activity, int textViewResourceId,
                                     List<Pair<String, NotePriority>> objects) {
        super(activity, textViewResourceId, objects);
        this.data = objects;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.priority, parent, false);

        View squareView = row.findViewById(R.id.square_view);
        TextView priorityLabel = (TextView) row.findViewById(R.id.priority_label);
        Pair<String, NotePriority> item = data.get(position);

        priorityLabel.setText(item.first);

        Drawable background = null;
        if (item.second == NotePriority.NORMAL) {
            background = getContext().getResources().getDrawable(R.drawable.priority_shape_green);
        } else if (item.second == NotePriority.MINOR) {
            background = getContext().getResources().getDrawable(R.drawable.priority_shape_lite_green);
        } else if (item.second == NotePriority.MAJOR) {
            background = getContext().getResources().getDrawable(R.drawable.priority_shape_orange);
        } else if (item.second == NotePriority.CRITICAL) {
            background = getContext().getResources().getDrawable(R.drawable.priority_shape_red);
        }

        if (background == null) {
            background = getContext().getResources().getDrawable(R.drawable.priority_shape_green);
        }

        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            squareView.setBackgroundDrawable(background);
        } else {
            squareView.setBackground(background);
        }

        return row;
    }

    public List<Pair<String, NotePriority>> getData() {
        return data;
    }
}
