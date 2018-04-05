package com.paulvarry.intra42.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.Campus;
import com.paulvarry.intra42.api.model.Cursus;
import com.paulvarry.intra42.cache.CacheCampus;
import com.paulvarry.intra42.cache.CacheCursus;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.Calendar;
import com.paulvarry.intra42.utils.ThemeHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final int PERMISSIONS_REQUEST_CALENDAR = 1;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);


                // Change Theme
                if (preference.getKey().equals(AppSettings.Theme.THEME)) {
                    // AppClass.changeTheme(AppSettings.Theme.getTheme(Integer.parseInt(stringValue)));

                    Intent i = AppClass.instance().getBaseContext().getPackageManager().getLaunchIntentForPackage(AppClass.instance().getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // AppClass.instance().startActivity(i);
                }

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }
            } else if (preference instanceof MultiSelectListPreference) {
                String sep = "";
                StringBuilder out = new StringBuilder();
                HashSet<String> selected = null;
                if (value instanceof HashSet<?>)
                    selected = (HashSet<String>) value;
                CharSequence[] preferenceEntries = ((MultiSelectListPreference) preference).getEntries();
                CharSequence[] preferenceValues = ((MultiSelectListPreference) preference).getEntryValues();
                HashMap<String, String> preferenceList = new HashMap<>();

                if (preferenceEntries != null && preferenceValues != null && preferenceEntries.length != preferenceValues.length && selected != null) {
                    for (int i = 0; preferenceEntries.length > i; i++) {
                        preferenceList.put(preferenceValues[i].toString(), preferenceEntries[i].toString());
                    }

                    for (String s : selected) {
                        out.append(preferenceList.get(s)).append(sep);
                        sep = ", ";
                    }

                    // Set the summary to reflect the new value.
                    preference.setSummary(out.toString());
                }
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                if (value instanceof String)
                    preference.setSummary(stringValue);
            }

            if (value instanceof Boolean && preference.getKey().equals(AppSettings.Notifications.ENABLE_NOTIFICATIONS)) {
                if ((boolean) value)
                    AppClass.scheduleAlarm(preference.getContext());
                else
                    AppClass.unscheduleAlarm(preference.getContext());
            } else if (preference.getKey().equals(AppSettings.Notifications.FREQUENCY)) {
                AppClass.scheduleAlarm(preference.getContext());
            }

            if (value instanceof String && preference.getKey().equals(AppSettings.Theme.THEME)) {
                AppClass app = AppClass.instance();
                if (app != null) {

                    Toast.makeText(app, R.string.pref_theme_info_need_restart, Toast.LENGTH_SHORT).show();
                }
            }

            return true;
        }
    };

    AppClass app;

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {

        if (preference == null)
            return;

        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
        Map<String, ?> sharedPreferencesAll = sharedPreferences.getAll();

        if (sharedPreferencesAll.get(preference.getKey()) instanceof String)
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, sharedPreferences.getString(preference.getKey(), ""));
        if (sharedPreferencesAll.get(preference.getKey()) instanceof Boolean)
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, sharedPreferences.getBoolean(preference.getKey(), false));
        if (sharedPreferencesAll.get(preference.getKey()) instanceof HashSet)
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, sharedPreferences.getStringSet(preference.getKey(), null));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CALENDAR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Calendar.setEnableCalendarWithAutoSelect(this, true);

                } else
                    Calendar.setEnableCalendarWithAutoSelect(this, true);
                recreate();
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        app = (AppClass) getApplication();
        ThemeHelper.setTheme(this, app);

        super.onCreate(savedInstanceState);

        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        AppBarLayout bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.include_actionbar, root, false);
        root.addView(bar, 0);
        Toolbar toolbar = bar.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ThemeHelper.setActionBar(bar, app);

        setupActionBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                ThemeHelper.setTheme(app);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ThemeHelper.setTheme(app);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBuildHeaders(final List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
        final SharedPreferences preferences = AppSettings.getSharedPreferences(this);

        for (Header h : target) {
            if (NotificationPreferenceFragment.class.getName().equals(h.fragment)) {
                if (AppSettings.Notifications.getNotificationsAllow(preferences))
                    h.summaryRes = R.string.pref_activated;
                else
                    h.summaryRes = R.string.pref_unactivated;
            } else if (ThemePreferenceFragment.class.getName().equals(h.fragment)) {
//                String[] keys = getResources().getStringArray(R.array.pref_theme_list_values);
//                String[] values = getResources().getStringArray(R.array.pref_theme_list_titles);
//                String k = app.themeSettings.key;
//
//                int pos = Arrays.binarySearch(keys, k);
//                String out = values[pos] + " - ";
                String out = getResources().getString(app.themeSettings.getName()) + " - ";
                if (app.themeSettings.isDark())
                    out += "Dark";
                else
                    out += "Light";
                h.summary = out;
            }
        }
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName)
                || NetworkPreferenceFragment.class.getName().equals(fragmentName)
                || AdvancedPreferenceFragment.class.getName().equals(fragmentName)
                || ThemePreferenceFragment.class.getName().equals(fragmentName)
                || AboutFragment.class.getName().equals(fragmentName);
    }


    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ThemePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_theme);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(AppSettings.Theme.THEME));
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if (preference.getTitleRes() == R.string.pref_theme_switch_dark_theme) {
                AppClass app = ((SettingsActivity) getActivity()).app;
                ThemeHelper.setTheme(app);
                ThemeHelper.setTheme(getActivity(), app);
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            final Activity activity = getActivity();
            AppClass app = (AppClass) activity.getApplication();
            app.mFirebaseAnalytics.setCurrentScreen(activity, activity.getClass().getSimpleName() + " -> " + getClass().getSimpleName(), null /* class override */);

            SparseArray<String> calendar = Calendar.getCalendarList(activity);

            ListPreference prefListCalendar = (ListPreference) findPreference(AppSettings.Notifications.LIST_CALENDAR);
            if (calendar != null) {
                CharSequence entryKey[] = new String[calendar.size()];
                CharSequence entryValues[] = new String[calendar.size()];

                for (int i = 0; i < calendar.size(); i++) {
                    int key = calendar.keyAt(i);
                    entryKey[i] = String.valueOf(key);
                    entryValues[i] = calendar.get(key);
                }
                prefListCalendar.setEntries(entryValues);
                prefListCalendar.setEntryValues(entryKey);
            }

            Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    updateCheckBoxSyncCalendarEnabled();

                    return true;
                }
            };
            final SwitchPreference prefCalendar = (SwitchPreference) findPreference(AppSettings.Notifications.ENABLE_CALENDAR);
            SwitchPreference prefNotifications = (SwitchPreference) findPreference(AppSettings.Notifications.ENABLE_CALENDAR);
            prefNotifications.setOnPreferenceChangeListener(listener);
            prefCalendar.setOnPreferenceChangeListener(listener);
            prefCalendar.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    updateCheckBoxSyncCalendarEnabled();
                    if (((SwitchPreference) preference).isChecked() && !AppSettings.Notifications.permissionCalendarEnable(activity)) {

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR},
                                SettingsActivity.PERMISSIONS_REQUEST_CALENDAR);
                        return true;
                    }
                    return false;
                }
            });

            if (!AppSettings.Notifications.permissionCalendarEnable(activity))
                prefCalendar.setChecked(false);

            updateCheckBoxSyncCalendarEnabled();


            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(AppSettings.Notifications.ENABLE_NOTIFICATIONS));
            bindPreferenceSummaryToValue(findPreference(AppSettings.Notifications.FREQUENCY));
            bindPreferenceSummaryToValue(findPreference(AppSettings.Notifications.LIST_CALENDAR));
        }

        void updateCheckBoxSyncCalendarEnabled() {
            final SwitchPreference prefCalendar = (SwitchPreference) findPreference(AppSettings.Notifications.ENABLE_CALENDAR);
            final SwitchPreference prefNotifications = (SwitchPreference) findPreference(AppSettings.Notifications.ENABLE_CALENDAR);
            final Preference prefSyncCalendar = findPreference(AppSettings.Notifications.CHECKBOX_SYNC_CALENDAR);

            if (prefCalendar.isChecked() && prefNotifications.isChecked())
                prefSyncCalendar.setEnabled(true);
            else
                prefSyncCalendar.setEnabled(false);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows network preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class NetworkPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_network);
            setHasOptionsMenu(true);

            Activity activity = getActivity();
            AppClass app = (AppClass) activity.getApplication();
            app.mFirebaseAnalytics.setCurrentScreen(activity, activity.getClass().getSimpleName() + " -> " + getClass().getSimpleName(), null /* class override */);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("list_preference_network_mobile_slow"));
            bindPreferenceSummaryToValue(findPreference("list_preference_network_mobile_fast"));
            bindPreferenceSummaryToValue(findPreference("list_preference_network_wifi"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows network preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class AboutFragment extends Fragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Activity activity = getActivity();
            AppClass app = (AppClass) activity.getApplication();
            app.mFirebaseAnalytics.setCurrentScreen(activity, activity.getClass().getSimpleName() + " -> " + getClass().getSimpleName(), null /* class override */);


        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment__basic, container, false);
        }
    }

    /**
     * This fragment shows advanced preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AdvancedPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_advanced);
            setHasOptionsMenu(true);

            Activity activity = getActivity();
            AppClass app = (AppClass) activity.getApplication();
            app.mFirebaseAnalytics.setCurrentScreen(activity, activity.getClass().getSimpleName() + " -> " + getClass().getSimpleName(), null /* class override */);

            List<Cursus> cursusCache = CacheCursus.get(app.cacheSQLiteHelper);
            ListPreference listPreferenceCursus = (ListPreference) findPreference(AppSettings.Advanced.PREFERENCE_ADVANCED_FORCE_CURSUS);
            if (listPreferenceCursus != null) {
                int cursusSize = cursusCache != null ? cursusCache.size() : 0;
                CharSequence entries[] = new String[cursusSize + 2];
                CharSequence entryValues[] = new String[cursusSize + 2];

                entries[0] = app.getString(R.string.pref_advanced_dont_force);
                entryValues[0] = "-1";
                entries[1] = app.getString(R.string.pref_value_all);
                entryValues[1] = "0";
                int i = 2;
                if (cursusCache != null)
                    for (Cursus cursus : cursusCache) {
                        entries[i] = cursus.name;
                        entryValues[i] = String.valueOf(cursus.id);
                        i++;
                    }
                listPreferenceCursus.setEntries(entries);
                listPreferenceCursus.setEntryValues(entryValues);
            }

            List<Campus> campusCache = CacheCampus.get(app.cacheSQLiteHelper);
            ListPreference listPreferenceCampus = (ListPreference) findPreference(AppSettings.Advanced.PREFERENCE_ADVANCED_FORCE_CAMPUS);
            if (listPreferenceCampus != null) {
                int campusSize = campusCache != null ? campusCache.size() : 0;
                CharSequence entries[] = new String[campusSize + 2];
                CharSequence entryValues[] = new String[campusSize + 2];

                entries[0] = app.getString(R.string.pref_advanced_dont_force);
                entryValues[0] = "-1";
                entries[1] = app.getString(R.string.pref_value_all);
                entryValues[1] = "0";
                int i = 2;
                if (campusCache != null)
                    for (Campus campus : campusCache) {
                        entries[i] = campus.name;
                        entryValues[i] = String.valueOf(campus.id);
                        i++;
                    }
                listPreferenceCampus.setEntries(entries);
                listPreferenceCampus.setEntryValues(entryValues);
            }

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("switch_preference_advanced_allow_friends"));
            bindPreferenceSummaryToValue(findPreference(AppSettings.Advanced.PREFERENCE_ADVANCED_FORCE_CURSUS));
            bindPreferenceSummaryToValue(findPreference(AppSettings.Advanced.PREFERENCE_ADVANCED_FORCE_CAMPUS));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
