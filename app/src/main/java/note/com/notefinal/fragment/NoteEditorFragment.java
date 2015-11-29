package note.com.notefinal.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import note.com.notefinal.MainActivity;
import note.com.notefinal.R;
import note.com.notefinal.entity.Note;
import note.com.notefinal.entity.Tag;
import note.com.notefinal.utils.dao.note.NoteDaoImpl;
import note.com.notefinal.utils.dao.tag.TagDaoImpl;

/**
 * Created by Иван on 08.11.2015.
 */
public class NoteEditorFragment extends Fragment {
    public static final String NAME = "noteEditor";

    private EditText titleField, descField;
    private Spinner tagField;
    private Note item;

    private MainActivity mainActivity;

    private NoteDaoImpl noteDao;
    private TagDaoImpl tagDaoImpl;

    private boolean isNew;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note_editor_portrait, container, false);

        noteDao = new NoteDaoImpl();
        tagDaoImpl = new TagDaoImpl();

        initFields(view);
        setItem();

        return view;
    }

    private void initFields(View view) {
        titleField = (EditText) view.findViewById(R.id.titleField);
        descField = (EditText) view.findViewById(R.id.descField);

        tagField = (Spinner) view.findViewById(R.id.tagField);
        List<Tag> tags = tagDaoImpl.getItems(note.com.notefinal.utils.dao.enums.View.FULL);
        List<String> options = new ArrayList<>();
        for (Tag tag : tags) {
            options.add(tag.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mainActivity.getBaseContext(),
                android.R.layout.simple_spinner_dropdown_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagField.setAdapter(adapter);
    }

    private void setItem() {
        if (getArguments() != null && getArguments().getParcelable("item") != null) {
            item = getArguments().getParcelable("item");
            titleField.setText(item.getTitle());
            descField.setText(item.getDescription());
            isNew = false;
        } else {
            isNew = true;
        }
    }

    public void commit() {
        if (validateFields()) {
            Note note = assembleItem();
            if (isNew) {
                noteDao.addItem(note);
            } else {
                noteDao.updateItem(note);
            }
            mainActivity.initListFragment(null);
        }
    }

    private Note assembleItem() {
        Note item;

        if (!isNew) {
            item = getArguments().getParcelable("item");
        } else {
            item = new Note();
            item.setId(UUID.randomUUID());
        }

        item.setTitle(titleField.getText().toString());
        item.setDescription(descField.getText().toString());
        if (item.getCreateTs() == null) {
            item.setCreateTs(new Date());
        }

        item.setTag(tagDaoImpl.findByName(tagField.getSelectedItem().toString(),
                note.com.notefinal.utils.dao.enums.View.FULL));

        return item;
    }

    private boolean validateFields() {
        boolean res = true;
        StringBuilder sb = new StringBuilder();
        sb.append("Fields:").append("\n");

        if (titleField.getText() == null || titleField.getText().length() == 0) {
            sb.append(" title, ");
            res = false;
        }

        if (descField.getText() == null || descField.getText().length() == 0) {
            sb.append(" description, ");
            res = false;
        }

        if (!res) {
            String message = sb.toString();
            message = message.substring(0, message.lastIndexOf(", "));
            message = String.format("%s %s", message, "can't be empty");
            createDialog(message);
        }

        return res;
    }

    private void createDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(message)
                .setCancelable(false).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alert.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        alert.getWindow().setAttributes(lp);
    }

    private int getCurrentOrientation() {
        return getResources().getConfiguration().orientation;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
}
