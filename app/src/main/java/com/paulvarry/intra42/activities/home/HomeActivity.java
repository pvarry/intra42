package com.paulvarry.intra42.activities.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ViewPagerAdapter;
import com.paulvarry.intra42.ui.BasicTabActivity;
import com.paulvarry.intra42.ui.tools.Navigation;
import com.paulvarry.intra42.utils.AppSettings;

public class HomeActivity extends BasicTabActivity
        implements HomeFragment.OnFragmentInteractionListener, HomeEventsFragment.OnFragmentInteractionListener,
        HomeSlotsFragment.OnFragmentInteractionListener, HomeCorrectionsFragment.OnFragmentInteractionListener,
        HomePastEventsFragment.OnFragmentInteractionListener {

    public static Intent getIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.activeHamburger();
        super.setSelectedMenu(Navigation.MENU_SELECTED_HOME);

        super.onCreate(savedInstanceState);

        if (app.me == null) {
            app.logoutAndRedirect();
        }
    }

    @Override
    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(HomeFragment.newInstance(), getString(R.string.tab_home_home));
        adapter.addFragment(HomeEventsFragment.newInstance(), getString(R.string.tab_home_agenda));
        adapter.addFragment(HomeSlotsFragment.newInstance(), getString(R.string.tab_home_slots));
        adapter.addFragment(HomeCorrectionsFragment.newInstance(), getString(R.string.tab_home_corrections));
        if (AppSettings.Advanced.getAllowPastEvents(this))
            adapter.addFragment(HomePastEventsFragment.newInstance(), getString(R.string.tab_home_past_events));
        viewPager.setAdapter(adapter);
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
