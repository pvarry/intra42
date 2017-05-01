package com.paulvarry.intra42.activities.users;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ViewPagerAdapter;
import com.paulvarry.intra42.ui.BasicActivity;
import com.paulvarry.intra42.ui.BasicTabActivity;
import com.paulvarry.intra42.ui.tools.Navigation;

public class UsersActivity
        extends BasicTabActivity
        implements UsersSearchFragment.OnFragmentInteractionListener, UsersAllFragment.OnFragmentInteractionListener,
        UsersAdvancedFragment.OnFragmentInteractionListener, UsersFriendsFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.activeHamburger();
        super.setSelectedMenu(Navigation.MENU_SELECTED_USERS);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return null;
    }

    @Override
    public StatusCode getDataOnOtherThread() {
        return StatusCode.FINISH;
    }

    @Override
    public StatusCode getDataOnMainThread() {
        return StatusCode.FINISH;
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

    @Override
    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        adapter.addFragment(UsersSearchFragment.newInstance(), getString(R.string.tab_users_search));
//        if (AppSettings.Advanced.getAllowFriends(this))
//            adapter.addFragment(UsersFriendsFragment.newInstance(), getString(R.string.tab_users_friends));
        adapter.addFragment(UsersAllFragment.newInstance(), getString(R.string.tab_users_all));
//        adapter.addFragment(UsersAdvancedFragment.newInstance(), getString(R.string.tab_users_advanced_search));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}
