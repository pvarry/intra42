package com.paulvarry.intra42.activities.settings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.Calendar;

/**
 * This fragment shows general preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
public class NotificationPreferenceFragment extends PreferenceFragmentCompat {

    private static final int PERMISSIONS_REQUEST_CALENDAR = 1;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_notification, rootKey);

        AppClass app = (AppClass) requireActivity().getApplication();
        app.mFirebaseAnalytics.setCurrentScreen(requireActivity(), requireActivity().getClass().getSimpleName() + " -> " + getClass().getSimpleName(), null /* class override */);

        setView();
    }

    private void setView() {
        updateCheckBoxSyncCalendarEnabled();
    }

    private void updateCalendarList() {
        SparseArray<String> calendar = Calendar.getCalendarList(requireActivity());
        ListPreference prefListCalendar = findPreference(AppSettings.Notifications.SELECTED_CALENDAR);
        if (prefListCalendar == null)
            return;

        if (calendar != null) {
            CharSequence[] entryKey = new String[calendar.size()];
            CharSequence[] entryValues = new String[calendar.size()];

            for (int i = 0; i < calendar.size(); i++) {
                int key = calendar.keyAt(i);
                entryKey[i] = String.valueOf(key);
                entryValues[i] = calendar.get(key);
            }
            prefListCalendar.setEntries(entryValues);
            prefListCalendar.setEntryValues(entryKey);
        }
    }

    private void updateCheckBoxSyncCalendarEnabled() {
        boolean enabled = AppSettings.Notifications.getCalendarSyncEnable(requireContext());
        final SwitchPreference prefCalendar = findPreference(AppSettings.Notifications.ENABLE_CALENDAR);
        prefCalendar.setChecked(enabled);
        updateCalendarList();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {

        if (!preference.hasKey())
            return super.onPreferenceTreeClick(preference);

        if (preference.getKey().equals(AppSettings.Notifications.ENABLE_NOTIFICATIONS) ||
                preference.getKey().equals(AppSettings.Notifications.FREQUENCY)) {

            if (AppSettings.Notifications.getNotificationsAllow(requireContext()))
                AppClass.scheduleAlarm(preference.getContext());
            else
                AppClass.unscheduleAlarm(preference.getContext());

        } else if (preference.getKey().contentEquals(AppSettings.Notifications.ENABLE_CALENDAR)) {

            if (AppSettings.Notifications.getCalendarSyncEnableNoPermissionCheck(requireContext()) &&
                    !AppSettings.Notifications.permissionCalendarEnable(requireActivity())) {
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR},
                        PERMISSIONS_REQUEST_CALENDAR);
                return true;
            }
        }

        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_CALENDAR) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Calendar.setEnableCalendarWithAutoSelect(requireContext(), true);
            }

            updateCheckBoxSyncCalendarEnabled();
        }
    }
}