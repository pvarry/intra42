package com.paulvarry.intra42.ui;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.SearchableActivity;
import com.paulvarry.intra42.activities.SettingsActivity;
import com.paulvarry.intra42.ui.tools.Navigation;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.Share;
import com.paulvarry.intra42.utils.SuperSearch;
import com.paulvarry.intra42.utils.Theme;
import com.paulvarry.intra42.utils.Tools;
import com.paulvarry.intra42.utils.UserImage;
import com.squareup.picasso.RequestCreator;

public abstract class BasicActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public AppClass app;
    public Toolbar toolbar;
    public MenuItem menuItemFilter;
    public MenuItem menuItemSpinner;
    public ProgressBar progressBarLoading;
    public AppBarLayout actionBar;
    protected ViewGroup coordinatorLayout;
    protected View viewContent;
    protected NavigationView navigationView;
    protected FloatingActionButton fabBaseActivity;
    MenuItem menuItemSearch;
    DrawerLayout drawer;
    private SimpleCursorAdapter searchAdapter;
    private ConstraintLayout constraintLayoutLoading;
    private ConstraintLayout constraintOnError;
    private TextView textViewLoading;
    private TextView textViewLoadingStatus;
    private Button buttonForceRefresh;
    private int drawerSelectedItemPosition = -1;
    @LayoutRes
    private int resContentId;
    private boolean activeHamburger = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (AppClass) getApplication();
        Theme.setTheme(this, app);

        super.setContentView(R.layout.activity__basic);

        toolbar = findViewById(R.id.toolbar);
        actionBar = findViewById(R.id.actionBar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        constraintLayoutLoading = findViewById(R.id.constraintLayoutLoading);
        constraintOnError = findViewById(R.id.constraintOnError);

        textViewLoading = findViewById(R.id.textViewLoading);
        textViewLoadingStatus = findViewById(R.id.textViewStatus);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        buttonForceRefresh = findViewById(R.id.buttonRefresh);

        fabBaseActivity = findViewById(R.id.fabBaseActivity);
        fabBaseActivity.setVisibility(View.GONE);

        setViewNavigation(); // set drawer menu

        // add content view programmatically

        if (resContentId == 0)
            throw new RuntimeException("ContentView must be set");
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewContent = inflater.inflate(resContentId, coordinatorLayout, false);
        coordinatorLayout.addView(viewContent);
        // CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) viewContent.getLayoutParams();
        // params.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        viewContent.requestLayout();

        setSupportActionBar(toolbar);
        Theme.setActionBar(actionBar, app);

        if (activeHamburger)
            setToggle();
        else
            setToggleNoHamburger();

        String toolbarName = getToolbarName();
        if (toolbarName != null) {
            setTitle(toolbarName);
            toolbar.setTitle(toolbarName);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        refresh();
    }

    /**
     * Use this method to set the content of the view. This method must be used on the {@link #onCreate(Bundle)}
     *
     * @param layoutResID The resource if of the content
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        this.resContentId = layoutResID;
    }

    protected void refresh() {
        setViewState(StatusCode.CONTENT);
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

        menuItemSpinner = menu.findItem(R.id.spinner);
        menuItemFilter = menu.findItem(R.id.filter);

        menuItemSearch = menu.findItem(R.id.search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menuItemSearch.getActionView();
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
            public boolean onSuggestionSelect(int position) {
                // Your code here
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                searchView.setQuery(SuperSearch.suggestionsReplace[position], false);
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                boolean ret;
                if (SuperSearch.open(BasicActivity.this, s)) {
                    ret = true;
                } else {
                    Bundle appData = new Bundle();
                    startSearch(s, true, appData, false);
                    ret = false;
                }
                searchView.clearFocus();
                menuItemSearch.collapseActionView();
                return ret;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                populateAdapter(s);
                return true;
            }
        });

        return true;
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
     * This is call when the user want to open this view on true Intra. Is triggered at the beginning to know if you want activate "show web version" on menu.
     *
     * @return The urls (on intra) to this page.
     */
    @Nullable
    abstract public String getUrlIntra();

    // You must implements your logic to get data using OrmLite
    private void populateAdapter(String query) {
        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "cityName"});
        for (int i = 0; i < SuperSearch.suggestions.length; i++) {
            if (SuperSearch.suggestions[i].toLowerCase().startsWith(query.toLowerCase()))
                c.addRow(new Object[]{i, SuperSearch.suggestions[i]});
        }
        searchAdapter.changeCursor(c);
    }

    /**
     * Hide all the view.
     */
    protected void setViewHide() {
        viewContent.setVisibility(View.GONE);
        constraintLayoutLoading.setVisibility(View.GONE);
        constraintOnError.setVisibility(View.GONE);
    }

    protected void setViewStateThread(final StatusCode state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setViewState(state);
            }
        });
    }

    protected void setViewState(StatusCode state) {
        setViewHide();

        String toolbarName = getToolbarName();
        if (toolbarName != null)
            toolbar.setTitle(toolbarName);

        switch (state) {
            case CONTENT:
                setViewStateContent();
                return;
            case LOADING:
                setViewStateLoading();
                return;
            case EMPTY:
                setViewStateEmpty();
                return;
            case API_DATA_ERROR:
                setViewStateApiError();
                return;
            case NETWORK_ERROR:
                setViewStateNetworkError();
                return;
            case API_UNAUTHORIZED:
                setViewStateUnauthorized();
        }
    }

    private void setViewStateContent() {
        if (viewContent != null)
            viewContent.setVisibility(View.VISIBLE);
        setViewContent();
    }

    private void setViewStateLoading() {
        constraintLayoutLoading.setVisibility(View.VISIBLE);
    }

    private void setViewStateEmpty() {
        constraintOnError.setVisibility(View.VISIBLE);

        String empty = getEmptyText();
        if (empty == null || empty.isEmpty())
            empty = getString(R.string.info_nothing_to_show);

        TextView textViewError = constraintOnError.findViewById(R.id.textViewError);
        textViewError.setText(empty);
    }

    private void setViewStateApiError() {
        Tools.setLayoutOnError(constraintOnError, R.drawable.ic_server_broken_black, R.string.info_error_on_loading_this_page, new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void setViewStateNetworkError() {
        Tools.setLayoutOnError(constraintOnError, R.drawable.ic_cloud_off_black_24dp, R.string.info_network_no_internet, new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void setViewStateUnauthorized() {
        Tools.setLayoutOnError(constraintOnError, R.drawable.ic_block_black_24dp, R.string.info_api_unauthorized, null);
    }

    /**
     * Set information about current loading page, for example "Loading user ...".
     *
     * @param loadingInfo Text to display.
     * @return Boolean if the info is well display.
     */
    public void setLoadingInfo(final String loadingInfo) {
        if (textViewLoading != null) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (loadingInfo != null) {
                        textViewLoading.setVisibility(View.VISIBLE);
                        textViewLoading.setText(loadingInfo);
                    } else
                        textViewLoading.setVisibility(View.GONE);
                }
            });

        }
    }

    /**
     * Set the current progress when loading data. Set the progress bar to indeterminate.
     *
     * @param progressStatus Text to display under the progress bar with progress information.
     * @return Boolean if the progress is well display.
     */
    public void setLoadingProgress(final String progressStatus) {
        if (textViewLoadingStatus != null) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setViewState(StatusCode.LOADING);
                    if (progressStatus != null) {
                        progressBarLoading.setIndeterminate(true);
                        textViewLoadingStatus.setVisibility(View.VISIBLE);
                        textViewLoadingStatus.setText(progressStatus);
                    } else
                        textViewLoadingStatus.setVisibility(View.GONE);
                }
            });

        }
    }

    /**
     * Set the current progress when loading data.
     *
     * @param progressStatus  Text to display under the progress bar with progress information.
     * @param currentProgress The current progress to display.
     * @param max             Progress when loading is supposedly finish.
     * @return Boolean if the progress is well display.
     */
    public void setLoadingProgress(final String progressStatus, final int currentProgress, final int max) {
        if (textViewLoadingStatus != null) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setViewState(StatusCode.LOADING);
                    if (progressStatus != null) {
                        progressBarLoading.setIndeterminate(false);
                        progressBarLoading.setMax(max);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            progressBarLoading.setProgress(currentProgress, true);
                        } else {
                            progressBarLoading.setProgress(currentProgress);
                        }
                        textViewLoadingStatus.setVisibility(View.VISIBLE);
                        textViewLoadingStatus.setText(progressStatus);
                    } else
                        textViewLoadingStatus.setVisibility(View.GONE);
                }
            });

        }
    }

    /**
     * Set the current progress when loading data.
     *
     * @param currentProgress The current progress to display.
     * @param max             Progress when loading is supposedly finish.
     * @return Boolean if the progress is well display.
     */
    public void setLoadingProgress(final int currentProgress, final int max) {
        String progress = "Loading page " + String.valueOf(currentProgress);
        if (max >= 0)
            progress += " " + "on" + " " + String.valueOf(max);
        else
            progress += " " + "on" + " " + "undetermined";
        setLoadingProgress(progress, currentProgress, max);
    }

    /**
     * Set the toggle (is the icon on the left on toolbar)
     */
    private void setToggle() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.accessibility_navigation_drawer_open, R.string.accessibility_navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();
    }

    private void setToggleNoHamburger() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.accessibility_navigation_drawer_open, R.string.accessibility_navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);

        setNoHamburger(toolbar);
    }

    private void setNoHamburger(Toolbar toolbar) {
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
            ImageView imageView = headerLayout.findViewById(R.id.imageViewNav);
            TextView name = headerLayout.findViewById(R.id.textViewNavName);
            TextView email = headerLayout.findViewById(R.id.textViewNavEmail);

            if (app.me != null) {
                name.setText(app.me.login);
                email.setText(app.me.email);
                RequestCreator p = UserImage.getPicassoRounded(this, app.me);
                if (p != null)
                    p.into(imageView);
            }

            if (drawerSelectedItemPosition != -1) {
                int size = navigationView.getMenu().size();
                for (int i = 0; i < size; i++) {
                    navigationView.getMenu().getItem(i).setChecked(false);
                }
                navigationView.getMenu().getItem(drawerSelectedItemPosition).setChecked(true);
            }

            if (app.me != null && AppSettings.getAppCampus(app) != 7)
                navigationView.getMenu().getItem(5).getSubMenu().getItem(3).setVisible(false);
        }
    }

    /**
     * If you went a hamburger menu.
     */
    protected void activeHamburger() {
        activeHamburger = true;
    }

    /**
     * Use to get the text on the toolbar, triggered when the activity start and after {@link BasicThreadActivity.GetDataOnThread#getDataOnOtherThread()} (only if it return true).
     *
     * @return Return the text on the toolbar.
     */
    abstract public String getToolbarName();

    /**
     * Run when activity build view, just after getting data.
     */
    abstract protected void setViewContent();

    /**
     * This text is useful when both {@link BasicThreadActivity.GetDataOnThread#getDataOnOtherThread()} and {@link BasicThreadActivity.GetDataOnMain#getDataOnMainThread()} return false.
     *
     * @return A simple text to display on screen, may return null;
     */
    abstract public String getEmptyText();

    protected enum StatusCode {
        /**
         * When a error obscure.
         */
        API_DATA_ERROR,
        /**
         * Set view error if something wrong append on loading data.
         */
        NETWORK_ERROR,
        API_UNAUTHORIZED,
        /**
         * When noting to display.
         */
        EMPTY,
        LOADING,
        CONTENT
    }
}
