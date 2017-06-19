package com.paulvarry.intra42.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Universal adapter for tabView
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    /**
     * A list with all fragments for the view.
     */
    private final List<Fragment> mFragmentList = new ArrayList<>();
    /**
     * A list a title with be added to the toolbar.
     */
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    /**
     * Use this function to add all the fragments to the adapter. The fragments are placed to the toolbar in order which they are added.
     *
     * @param fragment The Fragment to be added to the view.
     * @param title    The title for the fragment to be added.
     */
    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public List<String> getPageTitle() {
        return mFragmentTitleList;
    }
}
