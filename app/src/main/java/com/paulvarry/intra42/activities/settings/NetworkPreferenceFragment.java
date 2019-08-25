package com.paulvarry.intra42.activities.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;

import java.util.Objects;

/**
 * This fragment shows general preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
public class NetworkPreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_network, rootKey);

        AppClass app = (AppClass) Objects.requireNonNull(getActivity()).getApplication();
        app.mFirebaseAnalytics.setCurrentScreen(getActivity(), getActivity().getClass().getSimpleName() + " -> " + getClass().getSimpleName(), null /* class override */);
    }

    @Override
    public boolean onPreferenceTreeClick(androidx.preference.Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }
}