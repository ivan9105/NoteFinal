package note.com.notefinal.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

import note.com.notefinal.MainActivity;
import note.com.notefinal.R;
import note.com.notefinal.entity.Note;
import note.com.notefinal.utils.dao.note.NoteDaoImpl;

/**
 * Created by Иван on 08.11.2015.
 */
public class NoteEditorFragment extends Fragment {
    public static final String NAME = "noteEditor";

    private EditText titleField, descField;
    private Note item;

    private MainActivity mainActivity;

    private NoteDaoImpl noteDao;
    private boolean isNew;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note_editor_portrait, container, false);

        noteDao = new NoteDaoImpl();

        initFields(view);
        setItem();

        return view;
    }

    private void initFields(View view) {
        titleField = (EditText) view.findViewById(R.id.titleField);
        descField = (EditText) view.findViewById(R.id.descField);

        descField.addTextChangedListener(new TextWatcher() {
            private boolean change = true;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (change) {
                    String text = s.toString();
                    String[] words = text.split(" ");
                    StringBuilder sb = new StringBuilder();

                    boolean exist = false;
                    for (String word : words) {
                        if (word.length() < 20) {
                            sb.append(word).append(" ");
                        } else {
                            exist = true;
                            String first = word.substring(0, 19);
                            String second = word.substring(19, word.length());
                            sb.append(first).append(" ").append(second);
                        }
                    }

                    if (exist) {
                        String newText = sb.toString().trim();
                        change = false;
                        descField.setText(newText);
                    }
                } else {
                    descField.setSelection(descField.length());
                    change = true;
                }
            }
        });
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

        return item;
    }

    private boolean validateFields() {
        boolean res = true;
        StringBuilder sb = new StringBuilder();

        if (titleField.getText() == null || titleField.getText().length() == 0) {
            sb.append(" Title");
            res = false;
        }

        if (!res) {
            String message = sb.toString();
            message = String.format("%s %s", message, "can't be empty, continue edit?");
            createDialog(message);
        }

        return res;
    }

    private void createDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(message).setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mainActivity.initListFragment(null);
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
