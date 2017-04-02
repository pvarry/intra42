package com.paulvarry.intra42.ui;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.paulvarry.intra42.R;

public abstract class BasicTabActivity extends BasicActivity implements NavigationView.OnNavigationItemSelectedListener {

    public Toolbar toolbar;
    public TabLayout tabLayout;
    public ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setContentView(R.layout.activity__basic_tab);
        super.onCreate(savedInstanceState);
    }

    public void setNoHamburger(Toolbar toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setViewContent() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * Run after getting data: {@link BasicTabActivity#getDataOnOtherThread()}
     *
     * @param viewPager Current view pager (container of tabs)
     */
    abstract public void setupViewPager(ViewPager viewPager);

}
