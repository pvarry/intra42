package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.adapters.SectionListViewSearch;
import com.paulvarry.intra42.api.model.Locations;
import com.paulvarry.intra42.ui.BasicActivity;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.DateTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.halfbit.pinnedsection.PinnedSectionListView;
import retrofit2.Response;

public class LocationHistoryActivity extends BasicActivity implements BasicActivity.GetDataOnThread, AdapterView.OnItemClickListener {

    final static private String INTENT_LOCATION = "location";
    List<SectionListViewSearch.Item> items;
    PinnedSectionListView listView;
    List<Locations> locations;
    String host;
    SectionListViewSearch adapter;

    public static void openIt(Context context, String host) {
        Intent intent = new Intent(context, LocationHistoryActivity.class);
        intent.putExtra(INTENT_LOCATION, host);
        context.startActivity(intent);
    }

    public static void openIt(Context context, Locations locations) {
        Intent intent = new Intent(context, LocationHistoryActivity.class);
        intent.putExtra(INTENT_LOCATION, locations.host);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        host = getIntent().getStringExtra(INTENT_LOCATION);

        super.registerGetDataOnOtherThread(this);
        super.setContentView(R.layout.activity_location_history);
        super.onCreate(savedInstanceState);

        if (host == null)
            finish();

        listView = (PinnedSectionListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return null;
    }

    @Override
    public String getToolbarName() {
        return String.format(getString(R.string.format__host_s_history), host);
    }

    @Override
    public void setViewContent() {
        items = new ArrayList<>();

        for (int i = 0; i < locations.size(); i++) {
            if (i > 0 && i - 1 < locations.size()) {
                if (!DateTool.sameDayOf(locations.get(i).beginAt, locations.get(i - 1).beginAt))
                    items.add(new SectionListViewSearch.Item<Locations>(SectionListViewSearch.Item.SECTION, null, DateTool.getDateLong(locations.get(i).beginAt)));
            } else if (i == 0)
                items.add(new SectionListViewSearch.Item<Locations>(SectionListViewSearch.Item.SECTION, null, DateTool.getDateLong(locations.get(i).beginAt)));
            items.add(new SectionListViewSearch.Item<>(SectionListViewSearch.Item.ITEM, locations.get(i), null));
        }

        adapter = new SectionListViewSearch(this, items);
        listView.setAdapter(adapter);
    }

    @Override
    public String getEmptyText() {
        return null;
    }

    @Override
    public StatusCode getDataOnOtherThread() {

        try {
            Response<List<Locations>> response = app.getApiService().getLocationsHost(AppSettings.getAppCampus(app), host).execute();
            if (locations == null)
                locations = new ArrayList<>();
            if (response.isSuccessful()) {
                locations.addAll(response.body());
                return StatusCode.FINISH;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return StatusCode.ERROR;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserActivity.openIt(this, ((Locations) adapter.getItem(position).item).user, app);
    }
}
