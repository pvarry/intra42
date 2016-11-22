package com.paulvarry.intra42.tab.forum;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.paulvarry.intra42.Adapter.ViewPagerAdapter;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activity.NewTopicActivity;
import com.paulvarry.intra42.ui.BasicTabActivity;
import com.paulvarry.intra42.ui.tools.Navigation;

public class ForumActivity extends BasicTabActivity
        implements ForumUnreadFragment.OnFragmentInteractionListener, ForumLastTopicsFragment.OnFragmentInteractionListener, ForumTagFragment.OnFragmentInteractionListener, View.OnClickListener {

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.allowHamburger();
        super.onCreate(savedInstanceState);
        super.setSelectedMenu(Navigation.MENU_SELECTED_FORUM);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(this);
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return null;
    }

    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ForumUnreadFragment.newInstance(), "Unread");
        adapter.addFragment(ForumLastTopicsFragment.newInstance(), "Last topics");
        adapter.addFragment(ForumTagFragment.newInstance(), getString(R.string.tag));
        viewPager.setAdapter(adapter);
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
        return getString(R.string.forum);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View v) {
        if (v == fab)
            NewTopicActivity.openIt(this);
    }
}
