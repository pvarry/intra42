package com.paulvarry.intra42.ui;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.paulvarry.intra42.R;

public abstract class BasicTabActivity extends BasicThreadActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String SAVED_STATE_TAB_SELECTED = "tab_selected";

    public TabLayout tabLayout;
    public CustomViewPager viewPager;

    private int onRestartPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity__basic_tab);

        if (savedInstanceState != null)
            onRestartPosition = savedInstanceState.getInt(SAVED_STATE_TAB_SELECTED);
    }

    protected void setViewContent() {
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);

        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(onRestartPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        if (tabLayout != null)
            savedInstanceState.putInt(SAVED_STATE_TAB_SELECTED, tabLayout.getSelectedTabPosition());
    }

    /**
     * Run after getting data.
     *
     * @param viewPager Current view pager (container of tabs)
     */
    abstract protected void setupViewPager(ViewPager viewPager);

}
