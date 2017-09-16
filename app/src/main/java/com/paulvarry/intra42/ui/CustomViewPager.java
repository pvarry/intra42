package com.paulvarry.intra42.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.paulvarry.intra42.activities.user.UserProjectsFragment;
import com.paulvarry.intra42.adapters.ViewPagerAdapter;
import com.paulvarry.intra42.adapters.ViewStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class CustomViewPager extends ViewPager {

    private boolean enabled = true;
    private List<String> disableSwiping;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return canSwipe() && super.onInterceptTouchEvent(event);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return canSwipe() && super.onTouchEvent(event);

    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void disableSwiping(String tabName) {
        if (disableSwiping == null)
            disableSwiping = new ArrayList<>();
        disableSwiping.add(tabName);
    }

    boolean canSwipe() {
        Fragment fragment;
        String item;
        int currentItem = getCurrentItem();

        if (getAdapter() instanceof ViewStatePagerAdapter) {
            ViewStatePagerAdapter adapter = (ViewStatePagerAdapter) getAdapter();

            List<String> title = adapter.getPageTitle();
            item = title.get(currentItem);

            fragment = adapter.getItem(currentItem);
        } else {
            ViewPagerAdapter adapter = (ViewPagerAdapter) getAdapter();

            List<String> title = adapter.getPageTitle();
            item = title.get(currentItem);

            fragment = adapter.getItem(currentItem);
        }

        if (fragment instanceof UserProjectsFragment) {
            return ((UserProjectsFragment) fragment).canSwipe();
        }

        if (disableSwiping != null)
            for (String d : disableSwiping) {
                if (d.contains(item))
                    return false;
            }
        return enabled;
    }
}