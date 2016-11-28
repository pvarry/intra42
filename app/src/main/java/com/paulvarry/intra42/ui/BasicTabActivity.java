package com.paulvarry.intra42.ui;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.SuperSearch;
import com.paulvarry.intra42.Tools.UserImage;
import com.paulvarry.intra42.activity.SearchableActivity;
import com.paulvarry.intra42.activity.SettingsActivity;
import com.paulvarry.intra42.ui.tools.Navigation;
import com.squareup.picasso.RequestCreator;

public abstract class BasicTabActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public AppClass app;
    public Toolbar toolbar;
    public TabLayout tabLayout;
    public ViewPager viewPager;
    public MenuItem menuItemFilter;
    protected CoordinatorLayout coordinatorLayout;
    DrawerLayout drawer;
    NavigationView navigationView;
    MenuItem menuItemSearch;
    private ConstraintLayout constraintLayoutLoading;
    private ConstraintLayout constraintOnError;
    private TextView textViewLoadingStatus;
    private boolean allowHamburger = false;
    private SimpleCursorAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__basic_tab);

        app = (AppClass) this.getApplication();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        constraintLayoutLoading = (ConstraintLayout) findViewById(R.id.constraintLayoutLoading);
        constraintOnError = (ConstraintLayout) findViewById(R.id.constraintOnError);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        textViewLoadingStatus = (TextView) findViewById(R.id.textViewLoading);

        setViewNavigation();

        setSupportActionBar(toolbar);
        if (allowHamburger)
            setToggle();
        else
            setToggleNoHamburger();

        String toolbarName = getToolbarName();
        if (toolbarName != null) {
            setTitle(toolbarName);
            toolbar.setTitle(toolbarName);
        }

        if (getDataOnMainThread())
            setView();
        else {
            setViewLoading();
            new Thread(new Runnable() {
                @Override
                public void run() {

                    final boolean getDataSuccess = getDataOnOtherThread();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getDataSuccess) {
                                setView();
                            } else {
                                Toast.makeText(BasicTabActivity.this, "Can't open this", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });

                }
            }).start();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Handle clicks on ActionBar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection// Handle item selection
        switch (item.getItemId()) {

            case R.id.action_settings:
                final Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.action_open_intra:
                String url = getUrlIntra();
                if (url != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
                break;
        }
        return true;
    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Navigation.onNavigationItemSelected(this, item);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if (getUrlIntra() == null)
            menu.removeItem(R.id.action_open_intra);

        menuItemFilter = menu.findItem(R.id.filter);

        menuItemSearch = menu.findItem(R.id.search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItemSearch);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchableActivity.class)));

        final String[] from = new String[]{"cityName"};
        final int[] to = new int[]{android.R.id.text1};
        searchAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        searchView.setSuggestionsAdapter(searchAdapter);

        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "cityName"});
        for (int i = 0; i < SuperSearch.suggestions.length; i++) {
            c.addRow(new Object[]{i, SuperSearch.suggestions[i]});
        }
        searchAdapter.changeCursor(c);

        // Getting selected (clicked) item suggestion
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                searchView.setQuery(SuperSearch.suggestionsReplace[position], false);
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                // Your code here
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (SuperSearch.open(BasicTabActivity.this, s)) {
                    searchView.clearFocus();
                    menuItemSearch.collapseActionView();
                    return true;
                } else {
                    Bundle appData = new Bundle();
                    startSearch(s, true, appData, false);
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String s) {
                populateAdapter(s);
                return true;
            }
        });

        return true;
    }

    // You must implements your logic to get data using OrmLite
    private void populateAdapter(String query) {
        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "cityName"});
        for (int i = 0; i < SuperSearch.suggestions.length; i++) {
            if (SuperSearch.suggestions[i].toLowerCase().startsWith(query.toLowerCase()))
                c.addRow(new Object[]{i, SuperSearch.suggestions[i]});
        }
        searchAdapter.changeCursor(c);
    }

    private void setViewHide() {
        viewPager.setVisibility(View.GONE);
        constraintLayoutLoading.setVisibility(View.GONE);
        constraintOnError.setVisibility(View.GONE);
    }

    protected void setView() {
        setViewHide();
        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        String toolbarName = getToolbarName();
        if (toolbarName != null)
            toolbar.setTitle(toolbarName);
    }

    private void setViewLoading() {
        setViewHide();
        constraintLayoutLoading.setVisibility(View.VISIBLE);
    }

    public boolean setLoadingStatus(String text) {
        if (textViewLoadingStatus != null) {
            textViewLoadingStatus.setText(text);
            return true;
        }
        return false;
    }

    /**
     * Set the toggle (is the icon on the left on toolbar)
     */
    private void setToggle() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();
    }

    private void setToggleNoHamburger() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);

        setNoHamburger(toolbar);
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

    public void setSelectedMenu(int position) {
        navigationView.getMenu().getItem(position).setChecked(true);
    }

    /**
     * This function setup Navigation and Navigation's header (the menu)
     */
    private void setViewNavigation() {
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);

            View headerLayout = navigationView.getHeaderView(0);
            ImageView imageView = (ImageView) headerLayout.findViewById(R.id.imageViewNav);
            TextView name = (TextView) headerLayout.findViewById(R.id.textViewNavName);
            TextView email = (TextView) headerLayout.findViewById(R.id.textViewNavEmail);

            if (app.me != null) {
                name.setText(app.me.login);
                email.setText(app.me.email);
                RequestCreator p = UserImage.getPicassoRounded(this, app.me);
                if (p != null)
                    p.into(imageView);
            }
        }
    }

    protected void allowHamburger() {
        allowHamburger = true;
    }

    /**
     * This is call when the user want to open this view on true Intra.
     *
     * @return The urls (on intra) to this page.
     */
    @Nullable
    abstract public String getUrlIntra();

    /**
     * Burn after {@link BasicTabActivity#getDataOnOtherThread()}
     *
     * @param viewPager Current view pager (container of tabs)
     */
    abstract public void setupViewPager(ViewPager viewPager);

    /**
     * Burn before {@link BasicTabActivity#setupViewPager(ViewPager)}.
     * <p>
     * This method is run on a Thread, so you can make API calls and other long stuff.
     */
    abstract public boolean getDataOnOtherThread();

    /**
     * Triggered before {@link BasicTabActivity#setupViewPager(ViewPager)}.
     * <p>
     * This method is run on main Thread, so you can make api call.
     */
    abstract public boolean getDataOnMainThread();

    abstract public String getToolbarName();

}
