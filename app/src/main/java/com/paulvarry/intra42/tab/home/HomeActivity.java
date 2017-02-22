package com.paulvarry.intra42.tab.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.paulvarry.intra42.Adapter.ViewPagerAdapter;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activity.ExpertisesEditActivity;
import com.paulvarry.intra42.oauth.ServiceGenerator;
import com.paulvarry.intra42.tab.user.UserActivity;
import com.paulvarry.intra42.ui.BasicActivity;
import com.paulvarry.intra42.ui.BasicTabActivity;
import com.paulvarry.intra42.ui.tools.Navigation;

public class HomeActivity extends BasicTabActivity
        implements HomeFragment.OnFragmentInteractionListener, HomeEventsFragment.OnFragmentInteractionListener,
        HomeSlotsFragment.OnFragmentInteractionListener, HomeCorrectionsFragment.OnFragmentInteractionListener {

    public static Intent getIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.activeHamburger();
        super.setSelectedMenu(Navigation.MENU_SELECTED_HOME);

        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, ExpertisesEditActivity.class);
        startActivity(intent);
    }

    @Override
    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(HomeFragment.newInstance(), getString(R.string.tab_home_home));
        adapter.addFragment(HomeEventsFragment.newInstance(), getString(R.string.tab_home_agenda));
        adapter.addFragment(HomeSlotsFragment.newInstance(), getString(R.string.tab_home_slots));
        adapter.addFragment(HomeCorrectionsFragment.newInstance(), getString(R.string.tab_home_corrections));
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
    public boolean getDataOnOtherThread() {
        return true;
    }

    @Override
    public boolean getDataOnMainThread() {
        return true;
    }

    @Override
    public String getToolbarName() {
        return null;
    }

    /**
     * This text is useful when both {@link BasicActivity#getDataOnMainThread()} and {@link BasicActivity#getDataOnOtherThread()} return false.
     *
     * @return A simple text to display on screen, may return null;
     */
    @Override
    public String getEmptyText() {
        return null;
    }

}
