package com.paulvarry.intra42.activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.SectionListViewSearch;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.CursusUsers;
import com.paulvarry.intra42.api.model.Projects;
import com.paulvarry.intra42.api.model.Topics;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.SuperSearch;
import com.paulvarry.intra42.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchableActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    ConstraintLayout constraintLayoutLoading;
    SwipeRefreshLayout layoutResult;
    FrameLayout layoutApi;
    TextView textViewJson;
    View layoutOnError;

    TextView textViewLoading;
    ListView listView;
    Button buttonApiOpen;

    List<SectionListViewSearch.Item> items;

    AppClass app;
    ApiService apiService;
    String query;
    private SimpleCursorAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        constraintLayoutLoading = findViewById(R.id.constraintLayoutLoading);
        layoutResult = findViewById(R.id.layoutResult);
        layoutApi = findViewById(R.id.layoutApi);

        textViewLoading = findViewById(R.id.textViewLoading);
        listView = findViewById(R.id.listView);
        textViewJson = findViewById(R.id.textViewJson);
        buttonApiOpen = findViewById(R.id.buttonApiOpen);
        layoutOnError = findViewById(R.id.layoutOnError);

        app = (AppClass) getApplication();

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query != null) {
                setTitle(query);
            }
            doSearchSpitActions(query);
        } else {
            finish();
            Toast.makeText(this, "Can't open this", Toast.LENGTH_SHORT).show();
        }

        listView.setOnItemClickListener(this);
        layoutResult.setOnRefreshListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchAdapter = SuperSearch.setSearchSuggestionAdapter(this);

        searchItem.expandActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchableActivity.class)));
        searchView.clearFocus();
        searchView.setSuggestionsAdapter(searchAdapter);
        searchView.setQuery(query, false);

        populateAdapter(query);

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
                searchView.clearFocus();
                if (SuperSearch.open(SearchableActivity.this, s)) {
                    return true;
                } else {
                    doSearchSpitActions(s);
                    return true;
                }
            }

            @Override
            public boolean onQueryTextChange(String s) {
                populateAdapter(s);
                return true;
            }
        });

    /*
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do whatever you need
                return true; // KEEP IT TO TRUE OR IT DOESN'T OPEN !!
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                onBackPressed();
                return false; // OR FALSE IF YOU DIDN'T WANT IT TO CLOSE!
            }
        });*/

        return true;
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
                String url = "https://profile.intra.42.fr/searches/search?query=" + query;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
                break;
        }
        return true;
    }

    // You must implements your logic to get data using OrmLite
    private void populateAdapter(String query) {
        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "name"});
        for (int i = 0; i < SuperSearch.suggestions.length; i++) {
            if (SuperSearch.suggestions[i].toLowerCase().startsWith(query.toLowerCase()))
                c.addRow(new Object[]{i, SuperSearch.suggestions[i]});
        }
        searchAdapter.changeCursor(c);
    }

    private void doSearchSpitActions(String query) {
        if (query == null || query.isEmpty() || app == null || app.me == null) {
            finish();
            Toast.makeText(this, "Can't open this", Toast.LENGTH_SHORT).show();
            return;
        }
        this.query = query;
        apiService = app.getApiService();

        // Api call
        if (doSearchActionApiCall(query))
            return;

        // Basic search

        visibilityGoneAll();
        constraintLayoutLoading.setVisibility(View.VISIBLE);

        final String finalQuery = query;
        new Thread(new Runnable() {
            @Override
            public void run() {
                search(finalQuery);
            }
        }).start();
    }

    private void search(String query) {
        CursusUsers cursusUsers = app.me.getCursusUsersToDisplay(SearchableActivity.this);

        String[] split = query.split(" ");
        String stringToSearch;
        if (split.length > 1)
            stringToSearch = query.replaceFirst(split[0] + " ", "");
        else
            stringToSearch = query;

        Call<List<UsersLTE>> callUsersLogin = apiService.getUsersSearchLogin(stringToSearch);
        Call<List<UsersLTE>> callUsersFirstName = apiService.getUsersSearchFirstName(stringToSearch);
        Call<List<UsersLTE>> callUsersLastName = apiService.getUsersSearchLastName(stringToSearch);

        Call<List<Projects>> callProjects;
        if (cursusUsers != null)
            callProjects = apiService.getProjectsSearch(cursusUsers.cursusId, stringToSearch);
        else
            callProjects = apiService.getProjectsSearch(stringToSearch);

        Call<List<Topics>> callTopics;
        if (cursusUsers != null)
            callTopics = apiService.getTopicsSearch(cursusUsers.cursusId, stringToSearch);
        else
            callTopics = apiService.getTopicsSearch(stringToSearch);

        Response<List<UsersLTE>> responseUsersLogin = null;
        Response<List<UsersLTE>> responseUsersFirstName = null;
        Response<List<UsersLTE>> responseUsersLastName = null;
        Response<List<Projects>> responseProjects = null;
        Response<List<Topics>> responseTopics = null;

        try {

            if (split.length > 1) {

                if (SuperSearch.searchOnArray(R.array.search_users, split[0], SearchableActivity.this)) {
                    responseUsersLogin = execUsers(callUsersLogin);
                    responseUsersFirstName = execUsers(callUsersFirstName);
                    responseUsersLastName = execUsers(callUsersLastName);
                } else if (SuperSearch.searchOnArray(R.array.search_projects, split[0], SearchableActivity.this)) {
                    responseProjects = execProjects(callProjects);
                } else if (SuperSearch.searchOnArray(R.array.search_topics, split[0], SearchableActivity.this)) {
                    responseTopics = execTopics(callTopics);
                } else {
                    responseUsersLogin = execUsers(callUsersLogin);
                    responseUsersFirstName = execUsers(callUsersFirstName);
                    responseProjects = execProjects(callProjects);
                    responseTopics = execTopics(callTopics);
                }
            } else {
                responseUsersLogin = execUsers(callUsersLogin);
                responseUsersFirstName = execUsers(callUsersFirstName);
                responseUsersLastName = execUsers(callUsersLastName);
                responseProjects = execProjects(callProjects);
                responseTopics = execTopics(callTopics);
            }

            items = new ArrayList<>();

            boolean responseUsersLoginStatus = (responseUsersLogin != null && responseUsersLogin.isSuccessful() && responseUsersLogin.body() != null);
            boolean responseUsersFirstNameStatus = (responseUsersFirstName != null && responseUsersFirstName.isSuccessful() && responseUsersFirstName.body() != null);
            boolean responseUsersLastNameStatus = (responseUsersLastName != null && responseUsersLastName.isSuccessful() && responseUsersLastName.body() != null);
            if (responseUsersLoginStatus || responseUsersFirstNameStatus || responseUsersLastNameStatus)
                items.add(new SectionListViewSearch.Item<UsersLTE>(SectionListViewSearch.Item.SECTION, null, getString(R.string.search_section_users)));

            if (responseUsersLoginStatus) {
                for (UsersLTE u : responseUsersLogin.body())
                    items.add(new SectionListViewSearch.Item<>(SectionListViewSearch.Item.ITEM, u, u.getName()));
            }
            if (responseUsersFirstNameStatus) {
                for (UsersLTE u : responseUsersFirstName.body())
                    items.add(new SectionListViewSearch.Item<>(SectionListViewSearch.Item.ITEM, u, u.getName()));
            }
            if (responseUsersLastNameStatus) {
                for (UsersLTE u : responseUsersLastName.body())
                    items.add(new SectionListViewSearch.Item<>(SectionListViewSearch.Item.ITEM, u, u.getName()));
            }

            if (responseProjects != null && responseProjects.isSuccessful() && responseProjects.body() != null) {
                items.add(new SectionListViewSearch.Item<Projects>(SectionListViewSearch.Item.SECTION, null, getString(R.string.search_section_projects)));
                for (Projects p : responseProjects.body())
                    items.add(new SectionListViewSearch.Item<>(SectionListViewSearch.Item.ITEM, p, p.getName()));
            }

            if (responseTopics != null && responseTopics.isSuccessful() && responseTopics.body() != null) {
                items.add(new SectionListViewSearch.Item<Topics>(SectionListViewSearch.Item.SECTION, null, getString(R.string.search_section_topics)));
                for (Topics t : responseTopics.body())
                    items.add(new SectionListViewSearch.Item<>(SectionListViewSearch.Item.ITEM, t, t.getName()));
            }

            final SectionListViewSearch adapter = new SectionListViewSearch(SearchableActivity.this, items);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    visibilityGoneAll();
                    layoutResult.setVisibility(View.VISIBLE);
                    listView.setAdapter(adapter);
                }
            });
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    visibilityGoneAll();
                    layoutOnError.setVisibility(View.VISIBLE);
                    Tools.setLayoutOnError(layoutOnError, R.drawable.ic_cloud_off_black_24dp, R.string.info_network_error, SearchableActivity.this);
                }
            });
        }
    }

    private boolean doSearchActionApiCall(String query) {
        String[] split = query.split("\\.");
        if (split.length <= 1)
            return false;

        if (SuperSearch.searchOnArray(R.array.search_api, split[0], this)) {
            ApiService api = ((AppClass) getApplication()).getApiService();

            visibilityGoneAll();
            constraintLayoutLoading.setVisibility(View.VISIBLE);
            textViewLoading.setText(R.string.info_api_requesting);
            buttonApiOpen.setVisibility(View.GONE);

            String URL = split[1];
            if (URL.startsWith("v2/"))
                URL = "/" + URL;
            else if (!URL.startsWith("/v2/"))
                URL = "/v2/" + URL;
            else if (!URL.startsWith("/v2"))
                URL = "/v2" + URL;

            api.getOther(URL).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    visibilityGoneAll();
                    layoutApi.setVisibility(View.VISIBLE);

                    if (response.isSuccessful())
                        try {
                            JSONArray j = new JSONArray("[" + response.body().string() + "]");
                            Object o = j.get(0);
                            String output = null;
                            if (o instanceof JSONArray)
                                output = ((JSONArray) o).toString(4);
                            else if (o instanceof JSONObject)
                                output = ((JSONObject) o).toString(4);

                            textViewJson.setText(output);

                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    else {
                        try {
                            textViewJson.setText(response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    visibilityGoneAll();
                    layoutApi.setVisibility(View.VISIBLE);
                    Toast.makeText(SearchableActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
        return false;
    }

    Response<List<UsersLTE>> execUsers(Call<List<UsersLTE>> callUsers) throws IOException {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewLoading.setText(R.string.search_on_users);
            }
        });
        return callUsers.execute();
    }

    Response<List<Projects>> execProjects(Call<List<Projects>> callProjects) throws IOException {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewLoading.setText(R.string.search_on_projects);
            }
        });
        return callProjects.execute();
    }

    Response<List<Topics>> execTopics(Call<List<Topics>> callTopics) throws IOException {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewLoading.setText(R.string.search_on_topics);
            }
        });
        return callTopics.execute();
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query =
                    intent.getStringExtra(SearchManager.QUERY);
            doSearchSpitActions(query);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SectionListViewSearch.Item i = items.get(position);
        if (i.type == SectionListViewSearch.Item.ITEM)
            i.item.openIt(this);
    }

    private void visibilityGoneAll() {
        layoutResult.setVisibility(View.GONE);
        constraintLayoutLoading.setVisibility(View.GONE);
        layoutApi.setVisibility(View.GONE);
        layoutOnError.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        doSearchSpitActions(query);
    }
}
