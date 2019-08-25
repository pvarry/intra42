package com.paulvarry.intra42.activities.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.ThemeHelper;

public class SettingsActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private static final String TITLE_TAG = "settingsActivityTitle";
    AppBarLayout bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppClass app = (AppClass) getApplication();
        ThemeHelper.setTheme(this, app);
        super.onCreate(savedInstanceState);

        setupActionBar();
        setupView(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, getTitle());
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            this.finish();
        }
        return true;
    }

    /**
     * Set up the ActionBar
     */
    private void setupActionBar() {
        AppClass app = (AppClass) getApplication();

        LinearLayout root = findViewById(R.id.action_bar_root);
        bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.include_actionbar, root, false);
        root.addView(bar, 0);
        Toolbar toolbar = bar.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ThemeHelper.setActionBar(bar, app);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupView(Bundle savedInstanceState) {
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new HeaderFragment())
                    .commit();
        } else {
            setTitle(savedInstanceState.getCharSequence(TITLE_TAG));
        }
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                setTitle(R.string.title_activity_settings);
            }
        });
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        // Instantiate the new Fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, fragment)
                .addToBackStack(null)
                .commit();
        setTitle(pref.getTitle());
        return true;
    }

    public static class HeaderFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.pref_headers, rootKey);
            Context context = requireContext();

            String out = getResources().getString(AppSettings.Theme.getEnumTheme(context).getName()) + " - ";
            switch (AppSettings.Theme.getBrightness(context)) {
                case LIGHT:
                    out += context.getString(R.string.pref_theme_color_scheme_list_titles_light);
                    break;
                case DARK:
                    out += context.getString(R.string.pref_theme_color_scheme_list_titles_dark);
                    break;
                case SYSTEM:
                    out += context.getString(R.string.pref_theme_color_scheme_list_titles_auto);
                    break;
            }
            findPreference("header_preference_theme").setSummary(out);

            int summaryNotificationRes;
            if (AppSettings.Notifications.getNotificationsAllow(requireContext()))
                summaryNotificationRes = R.string.pref_activated;
            else
                summaryNotificationRes = R.string.pref_unactivated;
            findPreference("header_preference_notification").setSummary(summaryNotificationRes);

            int summaryAdvancedRes;
            if (AppSettings.Advanced.getAllowAdvanced(requireContext()))
                summaryAdvancedRes = R.string.pref_activated;
            else
                summaryAdvancedRes = R.string.pref_unactivated;
            findPreference("header_preference_advanced").setSummary(summaryAdvancedRes);
        }
    }
}
