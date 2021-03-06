package note.com.notefinal.fragment;


import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import note.com.notefinal.MainActivity;
import note.com.notefinal.R;
import note.com.notefinal.adapters.NoteEditorPriorityAdapter;
import note.com.notefinal.entity.Note;
import note.com.notefinal.entity.enums.NotePriority;
import note.com.notefinal.utils.DateUtil;
import note.com.notefinal.utils.dao.note.NoteDaoImpl;

/**
 * Created by Иван on 08.11.2015.
 */
public class NoteEditorFragment extends Fragment {
    public static final String NAME = "noteEditor";

    private EditText titleField, descField;
    private Note item;
    private Spinner priorityField;

    private MainActivity mainActivity;

    private NoteDaoImpl noteDao;
    private boolean isNew;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note_editor_portrait, container, false);

        noteDao = new NoteDaoImpl();

        initFields(view, inflater, container);
        setItem();
        postInit();

        return view;
    }

    private void postInit() {
        if (item != null && item.getPriority() != null) {
            NoteEditorPriorityAdapter adapter = (NoteEditorPriorityAdapter) priorityField.getAdapter();
            List<Pair<String, NotePriority>> data = adapter.getData();
            for (Pair<String, NotePriority> pair : data) {
                if (pair.second == item.getPriority()) {
                    priorityField.setSelection(data.indexOf(pair));
                    break;
                }
            }
        }
    }

    private void initFields(View view, LayoutInflater inflater, ViewGroup container) {
        titleField = (EditText) view.findViewById(R.id.titleField);
        descField = (EditText) view.findViewById(R.id.descField);
        priorityField = (Spinner) view.findViewById(R.id.priorityField);

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

        List<Pair<String, NotePriority>> data = new ArrayList<>();
        for (NotePriority priority : NotePriority.values()) {
            if (priority == NotePriority.NORMAL) {
                data.add(new Pair<>(mainActivity.getString(R.string.normal), priority));
            } else if (priority == NotePriority.MINOR) {
                data.add(new Pair<>(mainActivity.getString(R.string.minor), priority));
            } else if (priority == NotePriority.MAJOR) {
                data.add(new Pair<>(mainActivity.getString(R.string.major), priority));
            } else if (priority == NotePriority.CRITICAL) {
                data.add(new Pair<>(mainActivity.getString(R.string.critical), priority));
            }
        }
        priorityField.setAdapter(new NoteEditorPriorityAdapter(mainActivity, R.layout.priority, data));
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
        Note item = null;

        if (!isNew) {
            item = getArguments().getParcelable("item");
        }

        if (item == null) {
            item = new Note();
            item.setId(UUID.randomUUID());
        }

        item.setTitle(titleField.getText().toString());
        item.setDescription(descField.getText().toString());
        if (item.getCreateTs() == null) {
            item.setCreateTs(DateUtil.getCurrentDate());
        }

        int position = priorityField.getSelectedItemPosition();
        NoteEditorPriorityAdapter adapter = (NoteEditorPriorityAdapter) priorityField.getAdapter();
        List<Pair<String, NotePriority>> data = adapter.getData();
        Pair<String, NotePriority> pair = data.get(position);
        item.setPriority(pair.second);

        return item;
    }

    private boolean validateFields() {
        boolean res = true;
        StringBuilder sb = new StringBuilder();

        if (titleField.getText() == null || titleField.getText().length() == 0) {
            sb.append(mainActivity.getString(R.string.title));
            res = false;
        }

        if (!res) {
            String message = sb.toString();
            message = String.format("%s %s", message,
                    mainActivity.getString(R.string.note_editor_cancel_dialog));
            createDialog(message);
        }

        return res;
    }

    private void createDialog(String message) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.note_editor_cancel_dialog);
        dialog.setTitle(mainActivity.getString(R.string.info));

        dialog.getWindow().setGravity(Gravity.CENTER);

        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText(message);

        Button yesBtn = (Button) dialog.findViewById(R.id.yes);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        Button noBtn = (Button) dialog.findViewById(R.id.no);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                mainActivity.initListFragment(null);
            }
        });

        Point size = getSize();
        int height = size.y;

        RelativeLayout content = (RelativeLayout) dialog.findViewById(R.id.content);
        content.getLayoutParams().height = (height / 5);

        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setAttributes(layoutParams);

        dialog.show();
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private Point getSize() {
        Display display = mainActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }
}
