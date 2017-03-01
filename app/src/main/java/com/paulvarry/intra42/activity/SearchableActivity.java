package com.paulvarry.intra42.activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.MenuItemCompat;
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

import com.paulvarry.intra42.Adapter.SectionListViewSearch;
import com.paulvarry.intra42.ApiService;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.AppSettings;
import com.paulvarry.intra42.Tools.SuperSearch;
import com.paulvarry.intra42.api.Projects;
import com.paulvarry.intra42.api.Topics;
import com.paulvarry.intra42.api.UserLTE;

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

public class SearchableActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ConstraintLayout constraintLayoutLoading;
    SwipeRefreshLayout layoutResult;
    FrameLayout layoutApi;
    TextView textViewJson;

    TextView textViewLoading;
    ListView listView;
    Button buttonApiOpen;

    List<SectionListViewSearch.Item> items;

    ApiService apiService;
    String query;
    private SimpleCursorAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        constraintLayoutLoading = (ConstraintLayout) findViewById(R.id.constraintLayoutLoading);
        layoutResult = (SwipeRefreshLayout) findViewById(R.id.layoutResult);
        layoutApi = (FrameLayout) findViewById(R.id.layoutApi);

        textViewLoading = (TextView) findViewById(R.id.textViewLoading);
        listView = (ListView) findViewById(R.id.listView);
        textViewJson = (TextView) findViewById(R.id.textViewJson);
        buttonApiOpen = (Button) findViewById(R.id.buttonApiOpen);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query != null) {
                setTitle(query);
            }
            doSearch(query);
        } else {
            finish();
            Toast.makeText(this, "Can't open this", Toast.LENGTH_SHORT).show();
        }

        listView.setOnItemClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
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
                    doSearch(s);
                    return true;
                }
            }

            @Override
            public boolean onQueryTextChange(String s) {
                populateAdapter(s);
                return true;
            }
        });


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
        });

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

    private void doSearch(String query) {
        if (query == null || query.isEmpty()) {
            finish();
            Toast.makeText(this, "Can't open this", Toast.LENGTH_SHORT).show();
            return;
        }
        this.query = query;
        apiService = ((AppClass) getApplication()).getApiService();


        // Api call
        if (doSearchApiCall(query))
            return;

        // Basic search

        visibilityGoneAll();
        constraintLayoutLoading.setVisibility(View.VISIBLE);

        final String finalQuery = query;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int cursus = AppSettings.ContentOption.getCursus(SearchableActivity.this);
                String[] split = finalQuery.split(" ");
                String stringToSearch;
                if (split.length > 1)
                    stringToSearch = finalQuery.replaceFirst(split[0] + " ", "");
                else
                    stringToSearch = finalQuery;

                Call<List<UserLTE>> callUsers = apiService.getUsersSearch(stringToSearch);

                Call<List<Projects>> callProjects;
                if (cursus != -1 && cursus != 0)
                    callProjects = apiService.getProjectsSearch(cursus, stringToSearch);
                else
                    callProjects = apiService.getProjectsSearch(stringToSearch);

                Call<List<Topics>> callTopics;
                if (cursus != -1 && cursus != 0)
                    callTopics = apiService.getTopicsSearch(cursus, stringToSearch);
                else
                    callTopics = apiService.getTopicsSearch(stringToSearch);

                Response<List<UserLTE>> responseUsers = null;
                Response<List<Projects>> responseProjects = null;
                Response<List<Topics>> responseTopics = null;

                try {

                    if (split.length > 1) {

                        if (SuperSearch.searchOnArray(R.array.search_users, split[0], SearchableActivity.this)) {
                            responseUsers = execUsers(callUsers);
                        } else if (SuperSearch.searchOnArray(R.array.search_projects, split[0], SearchableActivity.this)) {
                            responseProjects = execProjects(callProjects);
                        } else if (SuperSearch.searchOnArray(R.array.search_topics, split[0], SearchableActivity.this)) {
                            responseTopics = execTopics(callTopics);
                        } else {
                            responseUsers = execUsers(callUsers);
                            responseProjects = execProjects(callProjects);
                            responseTopics = execTopics(callTopics);
                        }
                    } else {
                        responseUsers = execUsers(callUsers);
                        responseProjects = execProjects(callProjects);
                        responseTopics = execTopics(callTopics);
                    }

                    items = new ArrayList<>();

                    if (responseUsers != null && responseUsers.isSuccessful()) {
                        items.add(new SectionListViewSearch.Item<UserLTE>(SectionListViewSearch.Item.SECTION, null, getString(R.string.search_section_users)));
                        for (UserLTE u : responseUsers.body())
                            items.add(new SectionListViewSearch.Item<>(SectionListViewSearch.Item.ITEM, u, u.getName()));
                    }

                    if (responseProjects != null && responseProjects.isSuccessful()) {
                        items.add(new SectionListViewSearch.Item<Projects>(SectionListViewSearch.Item.SECTION, null, getString(R.string.search_section_projects)));
                        for (Projects p : responseProjects.body())
                            items.add(new SectionListViewSearch.Item<>(SectionListViewSearch.Item.ITEM, p, p.getName()));
                    }

                    if (responseTopics != null && responseTopics.isSuccessful()) {
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean doSearchApiCall(String query) {
        String[] split = query.split("\\.");
        if (split.length <= 1)
            return false;

        if (SuperSearch.searchOnArray(R.array.search_api, split[0], this)) {
            ApiService api = ((AppClass) getApplication()).getApiService();

            visibilityGoneAll();
            constraintLayoutLoading.setVisibility(View.VISIBLE);
            textViewLoading.setText(R.string.calling);
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

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
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

    Response<List<UserLTE>> execUsers(Call<List<UserLTE>> callUsers) throws IOException {
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
            doSearch(query);
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
    }
}
