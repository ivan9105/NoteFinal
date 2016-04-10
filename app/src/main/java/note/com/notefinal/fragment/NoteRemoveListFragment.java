package note.com.notefinal.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import note.com.notefinal.MainActivity;
import note.com.notefinal.R;
import note.com.notefinal.adapters.NotePriorityAdapter;
import note.com.notefinal.adapters.NoteRemoveListAdapter;
import note.com.notefinal.entity.Note;
import note.com.notefinal.entity.enums.NotePriority;
import note.com.notefinal.utils.dao.note.NoteDaoImpl;

/**
 * Created by Иван on 22.11.2015.
 */
public class NoteRemoveListFragment extends ListFragment {
    public static final String NAME = "noteRemoveList";

    private MainActivity mainActivity;
    private NoteDaoImpl noteDao;
    private Spinner priorityField;
    private EditText filterField;
    private LinearLayout searchLayout;
    private NotePriority selectedPriority = null;
    private String selectedText = null;

    public NoteRemoveListFragment() {
        noteDao = new NoteDaoImpl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.removed_list, container, false);

        searchLayout = (LinearLayout) view.findViewById(R.id.search_layout);
        priorityField = (Spinner) view.findViewById(R.id.priority_field);
        filterField = (EditText) view.findViewById(R.id.filter_field);
        ImageView closeFilter = (ImageView) view.findViewById(R.id.closeFilter);
        closeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterItems(null, null);
                showSearch(false);
                mainActivity.showSearch();
                filterField.setText(null);
            }
        });


        final List<NotePriority> data = new ArrayList<>(Arrays.asList(NotePriority.values()));
        data.add(0, null);
        priorityField.setAdapter(new NotePriorityAdapter(mainActivity, R.layout.priority_short, data));

        priorityField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPriority = data.get(position);
                filterItems(selectedText, selectedPriority);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        filterField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                selectedText = filterField.getText().toString();
                selectedText = selectedText.trim();
                filterItems(selectedText, selectedPriority);
            }
        });

        searchLayout.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final List<Note> data = noteDao.getItems();
        final NoteRemoveListAdapter adapter = new NoteRemoveListAdapter(
                getActivity().getApplicationContext(), data);
        setListAdapter(adapter);

        ListView list = getListView();

        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note item = data.get(position);
                if (adapter.getSelected().contains(item)) {
                    adapter.getSelected().remove(item);
                } else {
                    adapter.getSelected().add(item);
                }
                adapter.notifyDataSetChanged();
            }
        });

        View view = getView();

        if (view != null) {
            Button ok = (Button) view.findViewById(R.id.ok);
            Button selectAll = (Button) view.findViewById(R.id.selectAll);

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (Note note : adapter.getSelected()) {
                        noteDao.removeItem(note);
                    }
                    mainActivity.initListFragment(null);
                }
            });

            selectAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (Note note : data) {
                        if (!adapter.getSelected().contains(note)) {
                            adapter.getSelected().add(note);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void showSearch(boolean show) {
        if (show) {
            searchLayout.setVisibility(View.VISIBLE);
        } else {
            filterItems(null, null);
            searchLayout.setVisibility(View.GONE);
        }
    }

    public boolean isShowSearch() {
        return searchLayout.getVisibility() == View.VISIBLE;
    }

    private void filterItems(@Nullable String param, @Nullable NotePriority priority) {
        List<Note> notes;
        if ((param == null || param.equals("")) && priority == null) {
            notes = noteDao.getItems();
        } else {
            notes = noteDao.getItems(param, priority);
        }

        NoteRemoveListAdapter adapter = (NoteRemoveListAdapter) getListAdapter();
        adapter.clear();
        adapter.addAll(notes);
        adapter.notifyDataSetChanged();
    }
}
