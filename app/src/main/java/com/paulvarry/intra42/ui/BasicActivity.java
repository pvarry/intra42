package com.paulvarry.intra42.ui;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.AppSettings;
import com.paulvarry.intra42.Tools.Share;
import com.paulvarry.intra42.Tools.SuperSearch;
import com.paulvarry.intra42.Tools.UserImage;
import com.paulvarry.intra42.activity.SearchableActivity;
import com.paulvarry.intra42.activity.SettingsActivity;
import com.paulvarry.intra42.ui.tools.Navigation;
import com.squareup.picasso.RequestCreator;

public abstract class BasicActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public AppClass app;
    public Toolbar toolbar;
    public MenuItem menuItemFilter;
    protected CoordinatorLayout coordinatorLayout;
    protected View viewContent;
    protected NavigationView navigationView;
    protected FloatingActionButton fabBaseActivity;
    MenuItem menuItemSearch;
    DrawerLayout drawer;
    private SimpleCursorAdapter searchAdapter;
    private ConstraintLayout constraintLayoutLoading;
    private ConstraintLayout constraintOnError;
    private TextView textViewLoadingStatus;
    private TextView textViewError;
    private Button buttonForceRefresh;
    private int drawerSelectedItemPosition = -1;

    private boolean activeHamburger = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__basic);

        app = (AppClass) this.getApplication();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        constraintLayoutLoading = (ConstraintLayout) findViewById(R.id.constraintLayoutLoading);
        constraintOnError = (ConstraintLayout) findViewById(R.id.constraintOnError);

        textViewLoadingStatus = (TextView) findViewById(R.id.textViewLoading);
        textViewError = (TextView) findViewById(R.id.textViewError);
        buttonForceRefresh = (Button) findViewById(R.id.buttonRefresh);

        fabBaseActivity = (FloatingActionButton) findViewById(R.id.fabBaseActivity);
        fabBaseActivity.setVisibility(View.GONE);

        setViewNavigation(); // set drawer menu

        // add content view programmatically
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewContent = inflater.inflate(getViewContentResID(), coordinatorLayout, false);
        coordinatorLayout.addView(viewContent);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) viewContent.getLayoutParams();
        params.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        viewContent.requestLayout();

        setSupportActionBar(toolbar);
        if (activeHamburger)
            setToggle();
        else
            setToggleNoHamburger();

        String toolbarName = getToolbarName();
        if (toolbarName != null) {
            setTitle(toolbarName);
            toolbar.setTitle(toolbarName);
        }

        refresh();
    }

    protected void refresh() {
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
                                setViewError();
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
        String url = getUrlIntra();

        // Handle item selection// Handle item selection
        switch (item.getItemId()) {

            case R.id.action_settings:
                final Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.action_open_intra:
                if (url != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
                break;
            case R.id.action_share:
                Share.shareString(this, url);
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
        if (getUrlIntra() == null) {
            menu.removeItem(R.id.action_open_intra);
            menu.removeItem(R.id.action_share);
        }

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
                if (SuperSearch.open(BasicActivity.this, s)) {
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
        viewContent.setVisibility(View.GONE);
        constraintLayoutLoading.setVisibility(View.GONE);
        constraintOnError.setVisibility(View.GONE);
    }

    protected void setView() {
        setViewHide();

        String toolbarName = getToolbarName();
        if (toolbarName != null)
            toolbar.setTitle(toolbarName);

        if (viewContent != null)
            viewContent.setVisibility(View.VISIBLE);
        setViewContent();
    }

    private void setViewLoading() {
        setViewHide();
        constraintLayoutLoading.setVisibility(View.VISIBLE);
    }

    private void setViewError() {
        setViewHide();

        String toolbarName = getToolbarName();
        if (toolbarName != null)
            toolbar.setTitle(toolbarName);
        constraintOnError.setVisibility(View.VISIBLE);
        String error = getEmptyText();
        if (error == null)
            error = getString(R.string.nothing_to_show);
        textViewError.setText(error);
        buttonForceRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
    }

    /**
     * @param text Status text to put on loading view.
     * @return If everything finish good.
     */
    public boolean setLoadingStatus(final String text) {
        if (textViewLoadingStatus != null) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (text != null) {
                        textViewLoadingStatus.setVisibility(View.VISIBLE);
                        textViewLoadingStatus.setText(text);
                    } else
                        textViewLoadingStatus.setVisibility(View.GONE);
                }
            });

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
        drawerSelectedItemPosition = position;
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

            if (drawerSelectedItemPosition != -1)
                navigationView.getMenu().getItem(drawerSelectedItemPosition).setChecked(true);

            if (app.me != null && AppSettings.getAppCampus(app) != 7)
                navigationView.getMenu().getItem(5).getSubMenu().getItem(2).setVisible(false);
        }
    }

    /**
     * If you went a hamburger menu.
     */
    protected void activeHamburger() {
        activeHamburger = true;
    }

    /**
     * This is call when the user want to open this view on true Intra. Is triggered at the beginning to know if you want activate "show web version" on menu.
     *
     * @return The urls (on intra) to this page.
     */
    @Nullable
    abstract public String getUrlIntra();

    /**
     * Triggered when the activity start, after {@link BasicActivity#getDataOnMainThread()}.
     * <p>
     * This method is run on a Thread, so you can make API calls and other long stuff.
     *
     * @return Return true if something append on this method.
     */
    abstract public boolean getDataOnOtherThread();

    /**
     * Triggered when the activity start.
     * <p>
     * This method is run on main Thread, so you can make api call.
     *
     * @return Return true if something append on this method, if false -> the activity run {@link BasicActivity#getDataOnOtherThread()}.
     */
    abstract public boolean getDataOnMainThread();

    /**
     * Use to get the text on the toolbar, triggered when the activity start and after {@link BasicActivity#getDataOnOtherThread()} (only if it return true).
     *
     * @return Return the text on the toolbar.
     */
    abstract public String getToolbarName();

    /**
     * Burn when activity build view, after getting data.
     */
    abstract public void setViewContent();

    /**
     * @return Resource ID if a layout to be added on the view, this layout content all interested stuff for the activity.
     */
    @LayoutRes
    abstract public int getViewContentResID();

    /**
     * This text is useful when both {@link com.paulvarry.intra42.ui.BasicActivity#getDataOnMainThread()} and {@link com.paulvarry.intra42.ui.BasicActivity#getDataOnOtherThread()} return false.
     *
     * @return A simple text to display on screen, may return null;
     */
    abstract public String getEmptyText();
}
