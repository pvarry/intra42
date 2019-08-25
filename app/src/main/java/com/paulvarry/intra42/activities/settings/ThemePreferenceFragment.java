package com.paulvarry.intra42.activities.settings;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.ThemeHelper;

/**
 * This fragment shows general preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
public class ThemePreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private String rootKey;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        this.rootKey = rootKey;
        setPreferencesFromResource(R.xml.pref_theme, rootKey);
    }

    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        AppClass app = (AppClass) getActivity().getApplication();
        if (key.contentEquals(AppSettings.Theme.ACTIONBAR_BACKGROUND))
            ThemeHelper.setActionBar(((SettingsActivity) getActivity()).bar, app);

        if (key.contentEquals(AppSettings.Theme.THEME) ||
                key.contentEquals(AppSettings.Theme.BRIGHTNESS)) {
            ThemeHelper.setTheme(app);
            ThemeHelper.setTheme(getActivity(), app);
            ThemeHelper.setActionBar(((SettingsActivity) getActivity()).bar, app);
            setPreferencesFromResource(R.xml.pref_theme, rootKey);
        }
    }
}