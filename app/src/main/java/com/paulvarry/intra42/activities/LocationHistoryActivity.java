package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.clusterMap.ClusterMapActivity;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.adapters.LocationHeaderRecyclerAdapter;
import com.paulvarry.intra42.adapters.RecyclerItemSmall;
import com.paulvarry.intra42.adapters.SimpleHeaderRecyclerAdapter;
import com.paulvarry.intra42.api.model.Locations;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.DateTool;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Response;

public class LocationHistoryActivity extends BasicThreadActivity implements BasicThreadActivity.GetDataOnThread, SimpleHeaderRecyclerAdapter.OnItemClickListener<Locations> {

    final static private String INTENT_LOCATION = "location";
    final static private String INTENT_USER = "user";

    private RecyclerView recyclerView;
    private List<Locations> locations;
    private String host;
    private String login;
    private LocationHeaderRecyclerAdapter adapter;

    public static void openItWithLocation(Context context, String host) {
        Intent intent = new Intent(context, LocationHistoryActivity.class);
        intent.putExtra(INTENT_LOCATION, host);
        context.startActivity(intent);
    }

    public static void openItWithLocation(Context context, Locations locations) {
        openItWithLocation(context, locations.host);
    }

    public static void openItWithUser(Context context, String login) {
        Intent intent = new Intent(context, LocationHistoryActivity.class);
        intent.putExtra(INTENT_USER, login);
        context.startActivity(intent);
    }

    public static void openItWithUser(Context context, UsersLTE user) {
        openItWithUser(context, user.login);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        host = getIntent().getStringExtra(INTENT_LOCATION);
        login = getIntent().getStringExtra(INTENT_USER);

        super.setActionBarToggle(ActionBarToggle.ARROW);
        super.registerGetDataOnOtherThread(this);
        super.setContentView(R.layout.activity_location_history);

        if (host == null && login == null)
            finish();

        recyclerView = findViewById(R.id.recyclerView);

        super.onCreateFinished();
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return null;
    }

    @Override
    public String getToolbarName() {
        if (host != null)
            return String.format(getString(R.string.format__host_s_history), host);
        else
            return String.format(getString(R.string.format__host_s_history), login);
    }

    @Override
    public void setViewContent() {
        List<RecyclerItemSmall<Locations>> items = new ArrayList<>();
        Locations lastLocation = null;
        RecyclerItemSmall<Locations> lastHeader = null;
        List<Locations> locationInThisSection = null;

        for (Locations location : locations) {

            // ***** compute section (if needed) *****
            if (lastLocation == null ||
                    !DateTool.sameDayOf(lastLocation.beginAt, location.beginAt)) {

                if (login != null && locationInThisSection != null) { // add duration for last header
                    Date date = new Date();
                    boolean plusplus = false;
                    for (Locations locationTmp : locationInThisSection) {
                        if (locationTmp.beginAt != null && locationTmp.endAt != null)
                            date.setTime(date.getTime() - (locationTmp.beginAt.getTime() - locationTmp.endAt.getTime()));
                        else if (locationTmp.beginAt != null) {
                            date.setTime(date.getTime() - (locationTmp.beginAt.getTime() - new Date().getTime()));
                            plusplus = true;
                        }
                    }
                    lastHeader.title += " • " + DateTool.getDuration(date);
                    if (plusplus)
                        lastHeader.title += " ++";
                }

                // ***** compute item string *****
                lastHeader = new RecyclerItemSmall<>(DateTool.getDateLong(location.beginAt));
                items.add(lastHeader);
                locationInThisSection = new ArrayList<>();
            }

            locationInThisSection.add(location);

            items.add(new RecyclerItemSmall<>(location));
            lastLocation = location;
        }
        lastLocation = null;

//        title = (host != null ? location.user.login : location.host);
//        if (login != null) { // add duration ?
//            if (location.beginAt != null && location.endAt != null)
//                title += " • " + DateTool.getDuration(location.beginAt, location.endAt);
//            else if (location.beginAt != null)
//                title += " • " + DateTool.getDuration(location.beginAt, new Date()) + " ++";
//        }


        if (login != null && locationInThisSection != null) { // add duration for last element
            Date date = new Date();
            boolean plusplus = false;
            for (Locations locationTmp : locationInThisSection) {
                if (locationTmp.beginAt != null && locationTmp.endAt != null)
                    date.setTime(date.getTime() - (locationTmp.beginAt.getTime() - locationTmp.endAt.getTime()));
                else if (locationTmp.beginAt != null) {
                    date.setTime(date.getTime() - (locationTmp.beginAt.getTime() - new Date().getTime()));
                    plusplus = true;
                }
            }
            lastHeader.title += " • " + DateTool.getDuration(date);
            if (plusplus)
                lastHeader.title += " ++";
        }

        adapter = new LocationHeaderRecyclerAdapter(this, items);
        if (login != null)
            adapter.setUserHistory(true);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public String getEmptyText() {
        return null;
    }

    @Override
    public void getDataOnOtherThread() throws IOException, UnauthorizedException, ErrorServerException {

        Response<List<Locations>> response;
        if (host != null)
            response = app.getApiService().getLocationsHost(AppSettings.getAppCampus(app), host).execute();
        else
            response = app.getApiService().getLastLocations(login).execute();

        if (locations == null)
            locations = new ArrayList<>();
        if (Tools.apiIsSuccessful(response))
            locations.addAll(response.body());

    }

    @Override
    public void onItemClick(RecyclerItemSmall<Locations> item) {
        Locations location = item.item;
        if (location == null)
            return;

        if (host != null)
            UserActivity.openIt(this, location.user, app);
        else
            ClusterMapActivity.openIt(this, location.host);
    }
}
