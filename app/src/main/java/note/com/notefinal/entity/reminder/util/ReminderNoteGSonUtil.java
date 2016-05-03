package note.com.notefinal.entity.reminder.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import note.com.notefinal.entity.reminder.ReminderNote;
import note.com.notefinal.utils.CollectionUtil;

/**
 * Created by Иван on 28.04.2016.
 */
public class ReminderNoteGSonUtil {
    public static String toJSON(ReminderNote note) {
        String res = "";

        try {
            JSONObject jsonObject = new JSONObject();

            if (!CollectionUtil.isEmpty(note.getDates())) {
                JSONArray datesArray = new JSONArray();

                for (ReminderNote.ReminderDate date : note.getDates()) {
                    JSONObject dateObj = new JSONObject();
                    dateObj.put("id", date.getId().toString());
                    dateObj.put("date", date.getDate().getTime());
                    datesArray.put(dateObj);
                }

                jsonObject.put("dates", datesArray);
            }

            if (!CollectionUtil.isEmpty(note.getDays())) {
                JSONArray dayArray = new JSONArray();

                for (ReminderNote.ReminderDay day : note.getDays()) {
                    JSONObject dayObj = new JSONObject();
                    dayObj.put("id", day.getId().toString());
                    dayObj.put("day", day.getDay());

                    if (!CollectionUtil.isEmpty(day.getHours())) {
                        JSONArray hourArray = new JSONArray();

                        for (ReminderNote.ReminderHour hour : day.getHours()) {
                            JSONObject hourObj = new JSONObject();
                            hourObj.put("id", hour.getId());
                            hourObj.put("hour", hour.getHour().getTime());

                            hourArray.put(hourObj);
                        }

                        dayObj.put("hours", hourArray);
                    }
                }

                jsonObject.put("days", dayArray);
            }

            res = jsonObject.toString();
        } catch (JSONException ignore) {
        }

        return res;
    }

    public static void fromJSON(ReminderNote note) {
        try {
            if (note.getSettingsXml() != null && note.getSettingsXml().equals("")) {
                JSONObject jsonObject = new JSONObject(note.getSettingsXml());

                JSONArray datesArray = jsonObject.getJSONArray("dates");
                for (int i = 0; i < datesArray.length(); i++) {
                    JSONObject dateObj = datesArray.getJSONObject(i);
                    UUID id = UUID.fromString(dateObj.getString("id"));
                    Date date = new Date(dateObj.getLong("date"));

                    if (CollectionUtil.isEmpty(note.getDates())) {
                        note.setDates(new ArrayList<ReminderNote.ReminderDate>());
                    }

                    ReminderNote.ReminderDate date_ = note.new ReminderDate();
                    date_.setId(id);
                    date_.setDate(date);
                    note.getDates().add(date_);
                }

                JSONArray daysArray = jsonObject.getJSONArray("days");
                for (int i = 0; i < daysArray.length(); i++) {
                    JSONObject dayObj = daysArray.getJSONObject(i);
                    UUID id = UUID.fromString(dayObj.getString("id"));
                    Integer day = dayObj.getInt("day");

                    JSONArray hoursArray = dayObj.getJSONArray("hours");
                    List<ReminderNote.ReminderHour> hours = new ArrayList<>();
                    for (int j = 0; j < hoursArray.length(); j++) {
                        JSONObject hourObj = hoursArray.getJSONObject(j);
                        UUID hourId = UUID.fromString(hourObj.getString("id"));
                        Date hour_ = new Date(hourObj.getLong("hour"));

                        ReminderNote.ReminderHour hour = note.new ReminderHour();
                        hour.setId(hourId);
                        hour.setHour(hour_);
                        hours.add(hour);
                    }

                    ReminderNote.ReminderDay day_ = note.new ReminderDay();
                    day_.setId(id);
                    day_.setDay(day);
                    day_.setHours(hours);

                    if (CollectionUtil.isEmpty(note.getDays())) {
                        note.setDays(new ArrayList<ReminderNote.ReminderDay>());
                    }

                    note.getDays().add(day_);
                }
            }
        } catch (JSONException ignore) {
        }
    }
}
