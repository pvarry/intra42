package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.clusterMap.ClusterMapActivity;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.adapters.SectionListView;
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

import de.halfbit.pinnedsection.PinnedSectionListView;
import retrofit2.Response;

public class LocationHistoryActivity extends BasicThreadActivity implements BasicThreadActivity.GetDataOnThread, AdapterView.OnItemClickListener {

    final static private String INTENT_LOCATION = "location";
    final static private String INTENT_USER = "user";

    List<SectionListView.Item> items;
    PinnedSectionListView listView;
    List<Locations> locations;
    String host;
    String login;
    SectionListView adapter;

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

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

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
        items = new ArrayList<>();
        Locations curLocation;
        boolean needNewSection;
        String title;
        SectionListView.Item currentSectionTitle = null;
        List<Locations> locationInThisSection = null;

        for (int i = 0; i < locations.size(); i++) {
            curLocation = locations.get(i);
            needNewSection = false;

            // ***** compute section (if needed) *****
            if (i > 0 && i - 1 < locations.size()) {
                if (!DateTool.sameDayOf(locations.get(i).beginAt, locations.get(i - 1).beginAt)) // add a new section ?
                    needNewSection = true;
            } else if (i == 0)
                needNewSection = true;

            if (needNewSection) {

                if (login != null && locationInThisSection != null) { // add duration ?
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
                    currentSectionTitle.title += " • " + DateTool.getDuration(date);
                    if (plusplus)
                        currentSectionTitle.title += " ++";
                }

                currentSectionTitle = new SectionListView.Item<Locations>(SectionListView.Item.SECTION, null, DateTool.getDateLong(curLocation.beginAt));
                items.add(currentSectionTitle);
                locationInThisSection = new ArrayList<>();
            }

            // ***** compute item string *****
            title = (host != null ? curLocation.user.login : curLocation.host);
            if (login != null) { // add duration ?
                if (curLocation.beginAt != null && curLocation.endAt != null)
                    title += " • " + DateTool.getDuration(curLocation.beginAt, curLocation.endAt);
                else if (curLocation.beginAt != null)
                    title += " • " + DateTool.getDuration(curLocation.beginAt, new Date()) + " ++";
            }
            locationInThisSection.add(curLocation);

            items.add(new SectionListView.Item<>(SectionListView.Item.ITEM, curLocation, title));
        }

        if (login != null && locationInThisSection != null) { // add duration for last element ?
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
            currentSectionTitle.title += " • " + DateTool.getDuration(date);
            if (plusplus)
                currentSectionTitle.title += " ++";
        }

        adapter = new SectionListView(this, items);
        if (host != null)
            adapter.forceUserPicture(true);
        listView.setAdapter(adapter);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Locations location = ((Locations) adapter.getItem(position).item);

        if (location == null)
            return;

        if (host != null)
            UserActivity.openIt(this, location.user, app);
        else
            ClusterMapActivity.openIt(this, location.host);
    }
}
