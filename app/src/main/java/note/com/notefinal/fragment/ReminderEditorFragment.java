package note.com.notefinal.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import note.com.notefinal.R;
import note.com.notefinal.activities.ReminderActivity;
import note.com.notefinal.entity.reminder.ReminderNote;
import note.com.notefinal.utils.CollectionUtil;
import note.com.notefinal.utils.DateUtil;

/**
 * Created by Иван on 03.05.2016.
 */
public class ReminderEditorFragment extends Fragment {
    public static final String NAME = "reminderEditor";

    String[] daysArr;
    List<Map<String, String>> daysData;


    String[] groups = new String[]{"HTC", "Samsung", "LG"};

    String[] phonesHTC = new String[]{"Sensation", "Desire", "Wildfire", "Hero"};
    String[] phonesSams = new String[]{"Galaxy S II", "Galaxy Nexus", "Wave"};
    String[] phonesLG = new String[]{"Optimus", "Optimus Link", "Optimus Black", "Optimus One"};

    ArrayList<Map<String, String>> groupData;
    ArrayList<Map<String, String>> childDataItem;
    ArrayList<ArrayList<Map<String, String>>> childData;
    Map<String, String> m;

    ExpandableListView expandedList;
    private ReminderActivity reminderActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reminder_editor_portrait, container, false);
        Button add = (Button) view.findViewById(R.id.add);

        ReminderNote note = new ReminderNote();
        List<ReminderNote.ReminderDay> days = new ArrayList<>();
        days.add(createDay(note, 0));
        days.add(createDay(note, 2));
        days.add(createDay(note, 3));
        createHour(note, days.get(0), DateUtil.now());
        createHour(note, days.get(1), DateUtil.now());
        createHour(note, days.get(2), DateUtil.now());
        note.setDays(days);
        fillDayArr(days);
        daysData = new ArrayList<>();
        for (String dayStr : daysArr) {
            m = new HashMap<>();
            m.put("groupName", dayStr);
            daysData.add(m);
        }
        String groupFrom[] = new String[]{"groupName"};

        int groupTo[] = new int[]{android.R.id.text1};
        childData = new ArrayList<>();

        for (ReminderNote.ReminderDay day : days) {
            childDataItem = new ArrayList<>();
            for (ReminderNote.ReminderHour hour : day.getHours()) {
                m = new HashMap<>();
                m.put("phoneName", DateUtil.formattedTime(hour.getHour()));
                childDataItem.add(m);
            }
            childData.add(childDataItem);
        }

        String childFrom[] = new String[]{"phoneName"};
        // список ID view-элементов, в которые будет помещены аттрибуты элементов
        int childTo[] = new int[]{android.R.id.text1};

        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                reminderActivity,
                daysData,
                R.layout.reminder_expandable_list_item,
                groupFrom,
                groupTo,
                childData,
                R.layout.reminder_list_item,
                childFrom,
                childTo);

        expandedList = (ExpandableListView) view.findViewById(R.id.expandedList);
        expandedList.setAdapter(adapter);

        int groupCount = adapter.getGroupCount();
        for (int i = 0; i < groupCount; i++) {
            expandedList.expandGroup(i);
        }

        return view;
    }

    private void fillDayArr(List<ReminderNote.ReminderDay> days) {
        daysArr = new String[days.size()];
        int count = 0;
        for (ReminderNote.ReminderDay day : days) {
            switch (day.getDay()) {
                case 0:
                    daysArr[count] = reminderActivity.getString(R.string.Mo);
                    count++;
                    break;
                case 1:
                    daysArr[count] = reminderActivity.getString(R.string.Tu);
                    count++;
                    break;
                case 2:
                    daysArr[count] = reminderActivity.getString(R.string.We);
                    count++;
                    break;
                case 3:
                    daysArr[count] = reminderActivity.getString(R.string.Th);
                    count++;
                    break;
                case 4:
                    daysArr[count] = reminderActivity.getString(R.string.Fr);
                    count++;
                    break;
                case 5:
                    daysArr[count] = reminderActivity.getString(R.string.Sa);
                    count++;
                    break;
                case 6:
                    daysArr[count] = reminderActivity.getString(R.string.Su);
                    count++;
                    break;
            }
        }
    }

    public void setReminderActivity(ReminderActivity reminderActivity) {
        this.reminderActivity = reminderActivity;
    }

    private ReminderNote.ReminderDay createDay(ReminderNote note, int day) {
        ReminderNote.ReminderDay d = note.new ReminderDay();
        d.setId(UUID.randomUUID());
        d.setDay(day);
        return d;
    }

    private void createHour(ReminderNote note, ReminderNote.ReminderDay day,
                            Date hour) {
        ReminderNote.ReminderHour h = note.new ReminderHour();
        h.setId(UUID.randomUUID());
        h.setHour(hour);
        if (CollectionUtil.isEmpty(day.getHours())) {
            day.setHours(new ArrayList<ReminderNote.ReminderHour>());
        }
        day.getHours().add(h);
    }
}
