package com.paulvarry.intra42.activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.SectionListView;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.CursusUsers;
import com.paulvarry.intra42.api.model.Projects;
import com.paulvarry.intra42.api.model.Topics;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.utils.Analytics;
import com.paulvarry.intra42.utils.SuperSearch;
import com.paulvarry.intra42.utils.Tools;
import com.paulvarry.jsonviewer.JsonViewer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class SearchableActivity
        extends BasicThreadActivity
        implements AdapterView.OnItemClickListener, BasicThreadActivity.GetDataOnMain, BasicThreadActivity.GetDataOnThread {

    private LinearLayout layoutApi;
    private JsonViewer jsonViewer;
    private TextView textViewError;

    private ListView listView;

    List<SectionListView.Item> items;
    String apiRaw;

    AppClass app;
    ApiService apiService;
    String query;
    private SimpleCursorAdapter searchAdapter;
    private Object json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setContentView(R.layout.activity_searchable);


        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            if (query != null) {
                setTitle(query);
            }
        } else {
            finish();
            Toast.makeText(this, "Can't open this", Toast.LENGTH_SHORT).show();
        }


        registerGetDataOnOtherThread(this);
        registerGetDataOnMainTread(this);

        layoutApi = findViewById(R.id.layoutApiRawData);

        listView = findViewById(R.id.listView);
        jsonViewer = findViewById(R.id.jsonViewer);
        textViewError = findViewById(R.id.textViewError);

        app = (AppClass) getApplication();

        listView.setOnItemClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        super.onCreateFinished();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

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
                if (!SuperSearch.open(SearchableActivity.this, s)) {
                    query = s;
                    refresh();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                populateAdapter(s);
                return true;
            }
        });
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return false;
            }
        });

        return true;
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return "https://profile.intra.42.fr/searches/search?query=" + query;
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

    @Override
    public String getToolbarName() {
        return null;
    }

    @Override
    protected void setViewContent() {
        visibilityGoneAll();
        if (items != null) {
            final SectionListView adapter = new SectionListView(SearchableActivity.this, items);

            visibilityGoneAll();
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(adapter);
        } else {
            layoutApi.setVisibility(View.VISIBLE);
            if (json != null) {
                textViewError.setVisibility(View.GONE);
                jsonViewer.setVisibility(View.VISIBLE);
                jsonViewer.setJson(json);
            } else {
                textViewError.setVisibility(View.VISIBLE);
                jsonViewer.setVisibility(View.GONE);
                if (apiRaw != null)
                    textViewError.setText(apiRaw);
                else
                    textViewError.setText(R.string.error);
            }
        }
    }

    @Override
    public String getEmptyText() {
        return null;
    }

    @Override
    public ThreadStatusCode getDataOnMainThread() {
        if (query == null || query.isEmpty() || app == null || app.me == null) {
            finish();
            Toast.makeText(this, "Can't open this", Toast.LENGTH_SHORT).show();
            return ThreadStatusCode.FINISH;
        }
        return ThreadStatusCode.CONTINUE;
    }

    @Override
    public void getDataOnOtherThread() throws IOException, RuntimeException {
        apiService = app.getApiService();

        // Api call
        if (apiCallSearch(query))
            return;

        genericSearch(query);
    }

    private void genericSearch(String query) throws IOException {
        CursusUsers cursusUsers = app.me.getCursusUsersToDisplay(SearchableActivity.this);

        String[] split = query.split(" ");
        String stringToSearch;
        if (split.length > 1)
            stringToSearch = query.replaceFirst(split[0] + " ", "");
        else
            stringToSearch = query;

        Call<List<UsersLTE>> callUsersLogin = apiService.getUsersSearchLogin(stringToSearch);
//        Call<List<UsersLTE>> callUsersFirstName = apiService.getUsersSearchFirstName(stringToSearch);
//        Call<List<UsersLTE>> callUsersLastName = apiService.getUsersSearchLastName(stringToSearch);

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
//        Response<List<UsersLTE>> responseUsersFirstName = null;
//        Response<List<UsersLTE>> responseUsersLastName = null;
        Response<List<Projects>> responseProjects = null;
        Response<List<Topics>> responseTopics = null;

        if (split.length > 1) {

            if (SuperSearch.searchOnArray(R.array.search_users, split[0], SearchableActivity.this)) {
                Analytics.search("SEARCH_USERS", query);
                responseUsersLogin = execUsers(callUsersLogin, 1, 4);
//                responseUsersFirstName = execUsers(callUsersFirstName, 2, 4);
//                responseUsersLastName = execUsers(callUsersLastName, 3, 4);
            } else if (SuperSearch.searchOnArray(R.array.search_projects, split[0], SearchableActivity.this)) {
                Analytics.search("SEARCH_PROJECTS", query);
                responseProjects = execProjects(callProjects, 1, 2);
            } else if (SuperSearch.searchOnArray(R.array.search_topics, split[0], SearchableActivity.this)) {
                Analytics.search("SEARCH_TOPICS", query);
                responseTopics = execTopics(callTopics, 1, 2);
            } else {
                Analytics.search(null, query);
                responseUsersLogin = execUsers(callUsersLogin, 1, 5);
//                responseUsersFirstName = execUsers(callUsersFirstName, 2, 5);
                responseProjects = execProjects(callProjects, 3, 5);
                responseTopics = execTopics(callTopics, 4, 5);
            }
        } else {
            Analytics.search(null, query);
            responseUsersLogin = execUsers(callUsersLogin, 1, 6);
//            responseUsersFirstName = execUsers(callUsersFirstName, 2, 6);
//            responseUsersLastName = execUsers(callUsersLastName, 3, 6);
            responseProjects = execProjects(callProjects, 4, 6);
            responseTopics = execTopics(callTopics, 5, 6);
        }
        setLoadingProgress(getString(R.string.info_api_finishing), 1, 1);

        items = new ArrayList<>();

        if (responseUsersLogin != null && Tools.apiIsSuccessful(responseUsersLogin)) {
            items.add(new SectionListView.Item<Projects>(SectionListView.Item.SECTION, null, getString(R.string.search_section_users_login)));
            for (UsersLTE u : responseUsersLogin.body())
                items.add(new SectionListView.Item<>(SectionListView.Item.ITEM, u, u.getName(this)));
        }

//        if (responseUsersFirstName != null && Tools.apiIsSuccessful(responseUsersFirstName)) {
//            items.add(new SectionListView.Item<Projects>(SectionListView.Item.SECTION, null, getString(R.string.search_section_users_first_name)));
//            for (UsersLTE u : responseUsersFirstName.body())
//                items.add(new SectionListView.Item<>(SectionListView.Item.ITEM, u, u.getName(this)));
//        }
//
//        if (responseUsersLastName != null && Tools.apiIsSuccessful(responseUsersLastName)) {
//            items.add(new SectionListView.Item<Projects>(SectionListView.Item.SECTION, null, getString(R.string.search_section_users_last_name)));
//            for (UsersLTE u : responseUsersLastName.body())
//                items.add(new SectionListView.Item<>(SectionListView.Item.ITEM, u, u.getName(this)));
//        }

        if (responseProjects != null && Tools.apiIsSuccessful(responseProjects)) {
            items.add(new SectionListView.Item<Projects>(SectionListView.Item.SECTION, null, getString(R.string.search_section_projects)));
            for (Projects p : responseProjects.body())
                items.add(new SectionListView.Item<>(SectionListView.Item.ITEM, p, p.getName(this)));
        }

        if (responseTopics != null && Tools.apiIsSuccessful(responseTopics)) {
            items.add(new SectionListView.Item<Topics>(SectionListView.Item.SECTION, null, getString(R.string.search_section_topics)));
            for (Topics t : responseTopics.body())
                items.add(new SectionListView.Item<>(SectionListView.Item.ITEM, t, t.getName(this)));
        }
    }

    private boolean apiCallSearch(String query) throws IOException {
        String[] split = query.split("\\.");
        if (split.length <= 1)
            return false;

        if (SuperSearch.searchOnArray(R.array.search_api, split[0], this)) {
            ApiService api = ((AppClass) getApplication()).getApiService();

            setLoadingProgress(getString(R.string.info_api_requesting));

            String URL = split[1];
            if (URL.startsWith("v2/"))
                URL = "/" + URL;
            else if (!URL.startsWith("/v2/"))
                URL = "/v2/" + URL;
            else if (!URL.startsWith("/v2"))
                URL = "/v2" + URL;

            Call<ResponseBody> call = api.getOther(URL);
            Response<ResponseBody> response = call.execute();

            if (response.isSuccessful())
                apiRaw = response.body().string();
            else if (response.errorBody() != null) {
                apiRaw = response.errorBody().string();
            } else
                apiRaw = response.code() + " " + response.message();

            try {
                if (apiRaw.charAt(0) == '[')
                    json = new JSONArray(apiRaw);
                else if (apiRaw.charAt(0) == '{')
                    json = new JSONObject(apiRaw);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Analytics.search("API_CALL", query);
            return true;
        }
        return false;
    }

    Response<List<UsersLTE>> execUsers(Call<List<UsersLTE>> callUsers, int cur, int max) throws IOException {
        setLoadingProgress(getString(R.string.search_on_users), cur, max);
        return callUsers.execute();
    }

    Response<List<Projects>> execProjects(Call<List<Projects>> callProjects, int cur, int max) throws IOException {
        setLoadingProgress(getString(R.string.search_on_projects), cur, max);
        return callProjects.execute();
    }

    Response<List<Topics>> execTopics(Call<List<Topics>> callTopics, int cur, int max) throws IOException {
        setLoadingProgress(getString(R.string.search_on_topics), cur, max);
        return callTopics.execute();
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            this.query = intent.getStringExtra(SearchManager.QUERY);
            refresh();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SectionListView.Item i = items.get(position);
        if (i.type == SectionListView.Item.ITEM)
            i.item.openIt(this);
    }

    private void visibilityGoneAll() {
        layoutApi.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonExpand:
                jsonViewer.expandJson();
                break;
            case R.id.buttonCollapse:
                jsonViewer.collapseJson();
                break;
        }
    }
}
