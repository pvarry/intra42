package com.paulvarry.intra42.activities.home;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;

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

                    Calendar.setEnableCalendarAutoSelectCalendar(this, true);
                    refresh();

                } else
                    Calendar.setEnableCalendarAutoSelectCalendar(this, true);
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.activeHamburger();
        super.setSelectedMenu(Navigation.MENU_SELECTED_HOME);

        super.onCreate(savedInstanceState);

        if (app.me == null) {
            app.logoutAndRedirect();
        }

        /* final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        remoteConfig.fetch(0).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    // task successful. Activate the fetched data
                    remoteConfig.activateFetched();

                    //update views?
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
                    alertDialog.setMessage("This application must be updated.");
                    alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            String appPackageName = "com.paulvarry.intra42";
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));

                            // This flag is set to prevent the browser with the login form from showing in the history stack
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                            startActivity(intent);
                        }
                    });
                    alertDialog.create().show();
                }
            }
        });*/
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
        return "https://intra.42.fr/";
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
