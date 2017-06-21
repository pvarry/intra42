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
import com.paulvarry.intra42.utils.UserImage;
import com.squareup.picasso.RequestCreator;

public abstract class BasicActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public AppClass app;
    public Toolbar toolbar;
    public MenuItem menuItemFilter;
    public MenuItem menuItemSpinner;
    public ProgressBar progressBarLoading;
    protected CoordinatorLayout coordinatorLayout;
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
    private TextView textViewError;
    private Button buttonForceRefresh;
    private int drawerSelectedItemPosition = -1;
    @LayoutRes
    private int resContentId;
    private boolean activeHamburger = false;

    private GetDataOnMain getDataOnMain;
    private GetDataOnThread getDataOnTread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity__basic);

        app = (AppClass) getApplication();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        constraintLayoutLoading = (ConstraintLayout) findViewById(R.id.constraintLayoutLoading);
        constraintOnError = (ConstraintLayout) findViewById(R.id.constraintOnError);

        textViewLoading = (TextView) findViewById(R.id.textViewLoading);
        textViewLoadingStatus = (TextView) findViewById(R.id.textViewStatus);
        progressBarLoading = (ProgressBar) findViewById(R.id.progressBarLoading);
        textViewError = (TextView) findViewById(R.id.textViewError);
        buttonForceRefresh = (Button) findViewById(R.id.buttonRefresh);

        fabBaseActivity = (FloatingActionButton) findViewById(R.id.fabBaseActivity);
        fabBaseActivity.setVisibility(View.GONE);

        setViewNavigation(); // set drawer menu

        // add content view programmatically

        if (resContentId == 0)
            throw new RuntimeException("ContentView must be set");
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewContent = inflater.inflate(resContentId, coordinatorLayout, false);
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

        if (getDataOnMain != null) {
            StatusCode statusDataOnMain = getDataOnMain.getDataOnMainThread();

            if (statusDataOnMain == StatusCode.FINISH)
                setView();
            else if (statusDataOnMain == StatusCode.ERROR)
                setViewError();
            else if (statusDataOnMain == StatusCode.EMPTY)
                setViewEmpty();
        }
        if (getDataOnTread != null) {
            setViewLoading();
            new Thread(new Runnable() {
                @Override
                public void run() {

                    final StatusCode statusDataOnThread = getDataOnTread.getDataOnOtherThread();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (statusDataOnThread == StatusCode.FINISH || statusDataOnThread == StatusCode.CONTINUE)
                                setView();
                            else if (statusDataOnThread == StatusCode.ERROR)
                                setViewError();
                            else
                                setViewEmpty();
                        }
                    });

                }
            }).start();
        }
        if (getDataOnMain == null && getDataOnTread == null)
            setView();
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

    /**
     * Set view error if something wrong append on loading data or view.
     */
    protected void setViewEmpty() {

        String empty = getEmptyText();
        if (empty == null || empty.isEmpty())
            empty = getString(R.string.nothing_to_show);

        setViewError(true, empty);
    }

    /**
     * Set view error if something wrong append on loading data or view.
     */
    protected void setViewError() {
        setViewError(true, null);
    }

    /**
     * Set view error if something wrong append on loading data or view.
     *
     * @param errorText Text to display.
     */
    protected void setViewError(String errorText) {
        setViewError(true, errorText);
    }

    /**
     * Set view error if something wrong append on loading data or view.
     *
     * @param allowRefresh Allow to display a refresh button.
     * @param errorText    Text to display.
     */
    protected void setViewError(boolean allowRefresh, String errorText) {
        setViewHide();

        String toolbarName = getToolbarName();
        if (toolbarName != null)
            toolbar.setTitle(toolbarName);
        constraintOnError.setVisibility(View.VISIBLE);

        if (errorText == null)
            errorText = getString(R.string.error_on_loading_this_page);
        textViewError.setText(errorText);

        if (allowRefresh) {
            buttonForceRefresh.setVisibility(View.VISIBLE);
            buttonForceRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refresh();
                }
            });
        } else
            buttonForceRefresh.setVisibility(View.GONE);
    }

    /**
     * Set information about current loading page, for example "Loading user ...".
     *
     * @param loadingInfo Text to display.
     * @return Boolean if the info is well display.
     */
    public boolean setLoadingInfo(final String loadingInfo) {
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

            return true;
        }
        return false;
    }

    /**
     * Set the current progress when loading data. Set the progress bar to indeterminate.
     *
     * @param progressStatus Text to display under the progress bar with progress information.
     * @return Boolean if the progress is well display.
     */
    public boolean setLoadingProgress(final String progressStatus) {
        if (textViewLoadingStatus != null) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setViewLoading();
                    if (progressStatus != null) {
                        progressBarLoading.setIndeterminate(true);
                        textViewLoadingStatus.setVisibility(View.VISIBLE);
                        textViewLoadingStatus.setText(progressStatus);
                    } else
                        textViewLoadingStatus.setVisibility(View.GONE);
                }
            });

            return true;
        }
        return false;
    }

    /**
     * Set the current progress when loading data.
     *
     * @param progressStatus  Text to display under the progress bar with progress information.
     * @param currentProgress The current progress to display.
     * @param max             Progress when loading is supposedly finish.
     * @return Boolean if the progress is well display.
     */
    public boolean setLoadingProgress(final String progressStatus, final int currentProgress, final int max) {
        if (textViewLoadingStatus != null) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setViewLoading();
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

            return true;
        }
        return false;
    }

    /**
     * Set the current progress when loading data.
     *
     * @param currentProgress The current progress to display.
     * @param max             Progress when loading is supposedly finish.
     * @return Boolean if the progress is well display.
     */
    public boolean setLoadingProgress(final int currentProgress, final int max) {
        String progress = "Loading page " + String.valueOf(currentProgress);
        if (max >= 0)
            progress += " " + "on" + " " + String.valueOf(max);
        else
            progress += " " + "on" + " " + "undetermined";
        return setLoadingProgress(progress, currentProgress, max);
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
     * Use to get the text on the toolbar, triggered when the activity start and after {@link GetDataOnThread#getDataOnOtherThread()} (only if it return true).
     *
     * @return Return the text on the toolbar.
     */
    abstract public String getToolbarName();

    /**
     * Run when activity build view, just after getting data.
     */
    abstract protected void setViewContent();

    /**
     * This text is useful when both {@link GetDataOnThread#getDataOnOtherThread()} and {@link BasicActivity.GetDataOnMain#getDataOnMainThread()} return false.
     *
     * @return A simple text to display on screen, may return null;
     */
    abstract public String getEmptyText();

    public void registerGetDataOnMainTread(GetDataOnMain getDataOnMain) {
        this.getDataOnMain = getDataOnMain;
    }

    public void registerGetDataOnOtherThread(GetDataOnThread getDataOnTread) {
        this.getDataOnTread = getDataOnTread;
    }

    protected enum StatusCode {
        /**
         * When a error obscure.
         */
        ERROR,
        /**
         * When noting to display.
         */
        EMPTY,
        /**
         * When getting data is finish.
         */
        FINISH,
        /**
         * When need to get more data (on the otherThread).
         */
        CONTINUE
    }

    public interface GetDataOnMain {
        /**
         * Triggered when the activity start.
         * <p>
         * This method is run on main Thread, so you can make api call.
         *
         * @return Return StatusCode of what appending {@link GetDataOnThread#getDataOnOtherThread()}.
         */
        StatusCode getDataOnMainThread();
    }

    public interface GetDataOnThread {
        /**
         * Triggered when the activity start.
         * <p>
         * This method is run on main Thread, so you can make api call.
         *
         * @return Return StatusCode of what appending {@link BasicActivity.GetDataOnMain#getDataOnMainThread()}.
         */
        StatusCode getDataOnOtherThread();
    }
}
