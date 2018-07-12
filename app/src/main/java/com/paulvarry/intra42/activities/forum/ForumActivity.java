package com.paulvarry.intra42.activities.forum;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.NewTopicActivity;
import com.paulvarry.intra42.adapters.ViewStatePagerAdapter;
import com.paulvarry.intra42.ui.BasicTabActivity;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.ui.tools.Navigation;

public class ForumActivity extends BasicTabActivity
        implements ForumUnreadFragment.OnFragmentInteractionListener, ForumLastTopicsFragment.OnFragmentInteractionListener, ForumTagFragment.OnFragmentInteractionListener, View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.activeHamburger();
        super.setSelectedMenu(Navigation.MENU_SELECTED_FORUM);
        super.onCreateFinished();
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return null;
    }

    public void setupViewPager(ViewPager viewPager) {
        ViewStatePagerAdapter adapter = new ViewStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ForumUnreadFragment.newInstance(), getString(R.string.title_tab_forum_unread));
        adapter.addFragment(ForumLastTopicsFragment.newInstance(), getString(R.string.title_tab_forum_last_topics));
        adapter.addFragment(ForumTagFragment.newInstance(), getString(R.string.title_tab_forum_tag));
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
     * This text is useful when both {@link BasicThreadActivity.GetDataOnThread#getDataOnOtherThread()} and {@link BasicThreadActivity.GetDataOnMain#getDataOnMainThread()} return false.
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
