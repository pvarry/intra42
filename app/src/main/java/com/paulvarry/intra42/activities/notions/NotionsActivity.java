package com.paulvarry.intra42.activities.notions;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ViewStatePagerAdapter;
import com.paulvarry.intra42.ui.BasicTabActivity;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.ui.tools.Navigation;

public class NotionsActivity
        extends BasicTabActivity
        implements NotionsAllFragment.OnFragmentInteractionListener, NotionsTagFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.activeHamburger();
        super.setSelectedMenu(Navigation.MENU_SELECTED_ELEARNING);
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getToolbarName() {
        return null;
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

    public void setupViewPager(ViewPager viewPager) {
        ViewStatePagerAdapter adapter = new ViewStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(NotionsAllFragment.newInstance(), getString(R.string.title_tab_elearning_all));
        adapter.addFragment(NotionsTagFragment.newInstance(), getString(R.string.title_tab_elearning_tags));
        viewPager.setAdapter(adapter);
    }

    public String getUrlIntra() {
        return "https://elearning.intra.42.fr/";
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
