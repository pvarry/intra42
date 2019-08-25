package com.paulvarry.intra42.activities.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.Campus;
import com.paulvarry.intra42.api.model.Cursus;
import com.paulvarry.intra42.cache.CacheCampus;
import com.paulvarry.intra42.cache.CacheCursus;
import com.paulvarry.intra42.utils.AppSettings;

import java.util.List;

/**
 * This fragment shows general preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
public class AdvancedPreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_advanced, rootKey);

        final Activity activity = getActivity();
        final AppClass app = (AppClass) activity.getApplication();
        app.mFirebaseAnalytics.setCurrentScreen(activity, activity.getClass().getSimpleName() + " -> " + getClass().getSimpleName(), null /* class override */);

        List<Cursus> cursusCache = CacheCursus.get(app.cacheSQLiteHelper);
        List<Campus> campusCache = CacheCampus.get(app.cacheSQLiteHelper);

        // if cache is empty, get data from API
        if (cursusCache == null || cursusCache.isEmpty() ||
                campusCache == null || campusCache.isEmpty()) {
            Toast.makeText(app, R.string.info_loading_cache_cursus_campus, Toast.LENGTH_LONG).show();
            new Thread(() -> {
                final List<Cursus> cursusCacheTmp = CacheCursus.getAllowInternet(app.cacheSQLiteHelper, app);
                final List<Campus> campusCacheTmp = CacheCampus.getAllowInternet(app.cacheSQLiteHelper, app);
                activity.runOnUiThread(() -> initData(app, cursusCacheTmp, campusCacheTmp));
            }).start();
        } else
            initData(app, cursusCache, campusCache);
    }

    @Override
    public boolean onPreferenceTreeClick(androidx.preference.Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    void initData(Context context, List<Cursus> cursusCache, List<Campus> campusCache) {
        ListPreference listPreferenceCursus = findPreference(AppSettings.Advanced.PREFERENCE_ADVANCED_FORCE_CURSUS);
        if (listPreferenceCursus != null) {
            int cursusSize = cursusCache != null ? cursusCache.size() : 0;
            CharSequence[] entries = new String[cursusSize + 2];
            CharSequence[] entryValues = new String[cursusSize + 2];

            entries[0] = context.getString(R.string.pref_advanced_dont_force);
            entryValues[0] = "-1";
            entries[1] = context.getString(R.string.pref_value_all);
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

        ListPreference listPreferenceCampus = findPreference(AppSettings.Advanced.PREFERENCE_ADVANCED_FORCE_CAMPUS);
        if (listPreferenceCampus != null) {
            int campusSize = campusCache != null ? campusCache.size() : 0;
            CharSequence[] entries = new String[campusSize + 2];
            CharSequence[] entryValues = new String[campusSize + 2];

            entries[0] = context.getString(R.string.pref_advanced_dont_force);
            entryValues[0] = "-1";
            entries[1] = context.getString(R.string.pref_value_all);
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


    }
}