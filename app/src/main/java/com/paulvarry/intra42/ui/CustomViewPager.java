package com.paulvarry.intra42.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.paulvarry.intra42.activities.user.UserProjectsFragment;
import com.paulvarry.intra42.adapters.ViewPagerAdapter;

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
        ViewPagerAdapter adapter = (ViewPagerAdapter) getAdapter();
        int c = getCurrentItem();
        List<String> title = adapter.getPageTitle();

        if (title.size() <= c)
            return enabled;

        String item = title.get(c);

        Fragment fragment = adapter.getItem(getCurrentItem());
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