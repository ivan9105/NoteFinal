package note.com.notefinal.fragment;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import note.com.notefinal.MainActivity;
import note.com.notefinal.R;
import note.com.notefinal.entity.Note;
import note.com.notefinal.entity.enums.NotePriority;
import note.com.notefinal.fragment.dialog.DatePickerFragment;
import note.com.notefinal.utils.DateUtil;
import note.com.notefinal.utils.dao.note.NoteDao;
import note.com.notefinal.utils.dao.note.NoteDaoImpl;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Иван on 10.04.2016.
 */
public class NoteCalendarFragment extends Fragment implements EasyPermissions.PermissionCallbacks {
    public static final String NAME = "noteCalendar";
    private MainActivity mainActivity;

    GoogleAccountCredential mCredential;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};

    private Date fromDate;
    private Date toDate;
    private TextView result;
    private NoteDao<Note> noteDao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note_calendar, container, false);

        TextView fromField = (TextView) view.findViewById(R.id.fromField);
        TextView toField = (TextView) view.findViewById(R.id.toField);

        Button selectFrom = (Button) view.findViewById(R.id.selectFrom);
        Button selectTo = (Button) view.findViewById(R.id.selectTo);

        selectFrom.setOnClickListener(new DateOnClickListener(fromField, true));
        selectTo.setOnClickListener(new DateOnClickListener(toField, false));

        fromDate = DateUtil.beginDay(DateUtil.addDays(DateUtil.getCurrentDate(), -14));
        toDate = DateUtil.endDay(DateUtil.getCurrentDate());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        fromField.setText(sdf.format(fromDate));
        toField.setText(sdf.format(toDate));

        result = (TextView) view.findViewById(R.id.result);

        final Button clearAccount = (Button) view.findViewById(R.id.clearAccount);
        clearAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = mainActivity.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(PREF_ACCOUNT_NAME);
                editor.apply();
                mCredential.setSelectedAccountName(null);
            }
        });

        final Button getButton = (Button) view.findViewById(R.id.getButton);
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getButton.setEnabled(false);
                result.setText("");
                getResultsFromApi();
                getButton.setEnabled(true);
            }
        });

        mProgress = new ProgressDialog(mainActivity);
        mProgress.setMessage(mainActivity.getResources().getString(R.string.callingGoogleCalendarAPI));

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                mainActivity, Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        noteDao = new NoteDaoImpl();

        return view;
    }

    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            runTask();
        }
    }

    private void runTask() {
        if (!isDeviceOnline()) {
            result.setText(mainActivity.getResources().getString(R.string.noNetworkConnectionAvailable));
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                mainActivity, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = mainActivity.getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                runTask();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    mainActivity.getResources().getString(R.string.thisAppNeedsToAccessYourProfile),
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void commit() {
        mainActivity.initListFragment(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != Activity.RESULT_OK) {
                    String text = mainActivity.getResources().getString(R.string.thisAppRequiresGooglePlayServices) +
                            mainActivity.getResources().getString(R.string.thisAppRequiresGooglePlayServices_);
                    result.setText(
                            text);
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == Activity.RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                mainActivity.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == Activity.RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, mainActivity);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //do nothing
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //do nothing
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(mainActivity);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(mainActivity);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    private void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                mainActivity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Final Note")
                    .build();
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private List<String> getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime from = new DateTime(fromDate);
            DateTime to = new DateTime(toDate);
            List<String> eventStrings = new ArrayList<>();
            Events events = mService.events().list("primary")
                    .setMaxResults(10000)
                    .setTimeMin(from)
                    .setTimeMax(to)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();

            String added = mainActivity.getResources().getString(R.string.added);
            String updated = mainActivity.getResources().getString(R.string.updated);

            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    start = event.getStart().getDate();
                }
                String summary = event.getSummary() == null ? event.getId() : event.getSummary();
                String description = event.getDescription();
                String id = event.getId();

                Note find = noteDao.getItemByEventId(id);
                if (find == null) {
                    Note item = new Note();
                    item.setPriority(NotePriority.NORMAL);
                    item.setTitle(summary);
                    item.setDescription(description);
                    item.setCreateTs(start != null ? new Date(start.getValue()) : DateUtil.getCurrentDate());
                            item.setId(UUID.randomUUID());
                    item.setEventId(id);

                    eventStrings.add(String.format("%s: %s (%s)", added, summary, DateUtil.toString(item.getCreateTs())));
                    noteDao.addItem(item);
                } else {
                    find.setTitle(summary);
                    find.setDescription(description);
                    find.setCreateTs(start != null ? new Date(start.getValue()) : DateUtil.getCurrentDate());

                    eventStrings.add(String.format("%s: %s (%s)", updated, summary, DateUtil.toString(find.getCreateTs())));
                    noteDao.updateItem(find);
                }
            }
            return eventStrings;
        }

        @Override
        protected void onPreExecute() {
            result.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                result.setText(R.string.noResultsReturned);
            } else {
                output.add(0, mainActivity.getResources().getString(R.string.dataRetrievedUsing));
                result.setText(TextUtils.join("\n", output));
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            REQUEST_AUTHORIZATION);
                } else {
                    String error = String.format("%s\n", mainActivity.getResources().
                            getString(R.string.followingErrorOccurred)) + mLastError.getMessage();
                    result.setText(error);
                }
            } else {
                result.setText(mainActivity.getResources().getString(R.string.requestCancelled));
            }
        }
    }

    private class DateOnClickListener implements View.OnClickListener {
        private TextView textView;
        private boolean from;

        public DateOnClickListener(TextView textView, boolean from) {
            this.textView = textView;
            this.from = from;
        }

        @Override
        public void onClick(View v) {
            DatePickerFragment dateFragment = new DatePickerFragment();

            Calendar calender = Calendar.getInstance();
            Bundle args = new Bundle();
            args.putInt("year", calender.get(Calendar.YEAR));
            args.putInt("month", calender.get(Calendar.MONTH));
            args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
            dateFragment.setArguments(args);

            dateFragment.setCallBack(new TextOnDateSetListener(textView, from));
            dateFragment.show(mainActivity.getSupportFragmentManager(), "Date Picker");
        }
    }

    private class TextOnDateSetListener implements DatePickerDialog.OnDateSetListener {
        private TextView textView;
        private boolean from;

        public TextOnDateSetListener(TextView textView, boolean from) {
            this.textView = textView;
            this.from = from;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendar.set(Calendar.YEAR, year);

            if (from) {
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                fromDate = calendar.getTime();
            } else {
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                toDate = calendar.getTime();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
            textView.setText(sdf.format(calendar.getTime()));
        }
    }
}
