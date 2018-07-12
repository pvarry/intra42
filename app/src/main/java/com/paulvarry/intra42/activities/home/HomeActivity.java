package com.paulvarry.intra42.activities.home;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ViewPagerAdapter;
import com.paulvarry.intra42.fragments.EventFragment;
import com.paulvarry.intra42.ui.BasicTabActivity;
import com.paulvarry.intra42.ui.tools.Navigation;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.Calendar;

public class HomeActivity extends BasicTabActivity
        implements HomeFragment.OnFragmentInteractionListener, HomeEventsFragment.OnFragmentInteractionListener,
        HomeSlotsFragment.OnFragmentInteractionListener, HomeCorrectionsFragment.OnFragmentInteractionListener,
        HomePastEventsFragment.OnFragmentInteractionListener, EventFragment.OnFragmentInteractionListener {

    static final int PERMISSIONS_REQUEST_CALENDAR = 1;

    public static Intent getIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CALENDAR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Calendar.setEnableCalendarWithAutoSelect(this, true);
                    refresh();

                } else
                    Calendar.setEnableCalendarWithAutoSelect(this, true);
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActionBarToggle(ActionBarToggle.HAMBURGER);
        super.setSelectedMenu(Navigation.MENU_SELECTED_HOME);

        super.onCreateFinished();
    }

    @Override
    public void setupViewPager(ViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(HomeFragment.newInstance(), getString(R.string.title_tab_home_home));
        adapter.addFragment(HomeEventsFragment.newInstance(), getString(R.string.title_tab_home_agenda));
        adapter.addFragment(HomeSlotsFragment.newInstance(), getString(R.string.title_tab_home_slots));
        adapter.addFragment(HomeCorrectionsFragment.newInstance(), getString(R.string.title_tab_home_evaluation));
        if (AppSettings.Advanced.getAllowPastEvents(this))
            adapter.addFragment(HomePastEventsFragment.newInstance(), getString(R.string.title_tab_home_past_events));
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                if (adapter.getCount() > position)
                    app.mFirebaseAnalytics.setCurrentScreen(HomeActivity.this, "User Profile -> " + adapter.getItem(position).getClass().getSimpleName(), null /* class override */);
            }
        });
        app.mFirebaseAnalytics.setCurrentScreen(HomeActivity.this, "User Profile -> " + HomeFragment.class.getSimpleName(), null /* class override */);
    }

    @Override
    public String getUrlIntra() {
        return getString(R.string.base_url_intra);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public String getToolbarName() {
        return null;
    }

    @Override
    public String getEmptyText() {
        return null;
    }

}
