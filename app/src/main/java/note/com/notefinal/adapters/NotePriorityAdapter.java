package note.com.notefinal.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
 * Created by Иван on 07.04.2016.
 */
public class NotePriorityAdapter extends ArrayAdapter<NotePriority> {
    private List<NotePriority> data;
    private LayoutInflater inflater;
    private MainActivity activity;

    public NotePriorityAdapter(MainActivity activity, int textViewResourceId,
                                     List<NotePriority> objects) {
        super(activity, textViewResourceId, objects);
        this.data = objects;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent, true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent, false);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent, boolean drop) {
        View row = inflater.inflate(
                (drop ? R.layout.priority_short_drop : R.layout.priority_short), parent, false);

        TextView squareView = (TextView) row.findViewById(R.id.square_view);
        NotePriority item = getItem(position);
        Drawable background = null;

        if (item == NotePriority.NORMAL) {
            background = getContext().getResources().getDrawable(R.drawable.priority_shape_green);
        } else if (item == NotePriority.MINOR) {
            background = getContext().getResources().getDrawable(R.drawable.priority_shape_lite_green);
        } else if (item == NotePriority.MAJOR) {
            background = getContext().getResources().getDrawable(R.drawable.priority_shape_orange);
        } else if (item == NotePriority.CRITICAL) {
            background = getContext().getResources().getDrawable(R.drawable.priority_shape_red);
        }

        if (item == null) {
            squareView.setText(activity.getString(R.string.all));
        } else {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                squareView.setBackgroundDrawable(background);
            } else {
                squareView.setBackground(background);
            }
        }

        return row;
    }
}
