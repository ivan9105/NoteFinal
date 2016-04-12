package note.com.notefinal.fragment.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Иван on 11.04.2016.
 */
public class DatePickerFragment extends DialogFragment {
    DatePickerDialog.OnDateSetListener onDateSetListener;

    public DatePickerFragment() {}

    private int year, month, day;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        year = args.getInt("year");
        month = args.getInt("month");
        day = args.getInt("day");
    }

    public void setCallBack(DatePickerDialog.OnDateSetListener onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), onDateSetListener, year, month, day);
    }
}
