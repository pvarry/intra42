package com.paulvarry.intra42.activities.forum;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.NewTopicActivity;
import com.paulvarry.intra42.adapters.ViewPagerAdapter;
import com.paulvarry.intra42.ui.BasicActivity;
import com.paulvarry.intra42.ui.BasicTabActivity;
import com.paulvarry.intra42.ui.tools.Navigation;

public class ForumActivity extends BasicTabActivity
        implements ForumUnreadFragment.OnFragmentInteractionListener, ForumLastTopicsFragment.OnFragmentInteractionListener, ForumTagFragment.OnFragmentInteractionListener, View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.activeHamburger();
        super.setSelectedMenu(Navigation.MENU_SELECTED_FORUM);
        super.onCreate(savedInstanceState);
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
        adapter.addFragment(ForumTagFragment.newInstance(), getString(R.string.title_activity_tag));
        viewPager.setAdapter(adapter);

        fabBaseActivity.setVisibility(View.VISIBLE);
        fabBaseActivity.setImageResource(R.drawable.ic_add_black_24dp);
        fabBaseActivity.setOnClickListener(this);
    }

    @Override
    public String getToolbarName() {
        return getString(R.string.navigation_forum);
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
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View v) {
        if (v == fabBaseActivity)
            NewTopicActivity.openIt(this);
    }
}
