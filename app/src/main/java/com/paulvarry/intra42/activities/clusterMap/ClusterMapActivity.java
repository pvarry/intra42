package com.paulvarry.intra42.activities.clusterMap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ViewPagerAdapter;
import com.paulvarry.intra42.api.model.Campus;
import com.paulvarry.intra42.api.model.Locations;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.cache.CacheCampus;
import com.paulvarry.intra42.ui.BasicActivity;
import com.paulvarry.intra42.ui.BasicTabActivity;
import com.paulvarry.intra42.utils.AppSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Response;

public class ClusterMapActivity extends BasicTabActivity implements ClusterMapFragment.OnFragmentInteractionListener, BasicActivity.GetDataOnMain, BasicActivity.GetDataOnThread {

    final static private String ARG_LOCATION_HIGHLIGHT = "location_highlight";
    HashMap<String, UsersLTE> locations;
    List<Campus> campus = new ArrayList<>();
    int campusId;
    String locationHighlight;

    public static void openIt(Context context) {
        Intent intent = new Intent(context, ClusterMapActivity.class);
        context.startActivity(intent);
    }

    public static void openIt(Context context, String location) {
        Intent intent = new Intent(context, ClusterMapActivity.class);
        intent.putExtra(ARG_LOCATION_HIGHLIGHT, location);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent i = getIntent();
        if (i != null && i.hasExtra(ARG_LOCATION_HIGHLIGHT))
            locationHighlight = i.getStringExtra(ARG_LOCATION_HIGHLIGHT);

        super.activeHamburger();

        registerGetDataOnOtherThread(this);
        registerGetDataOnMainTread(this);

        super.onCreate(savedInstanceState);
        campusId = AppSettings.getAppCampus(app);
        navigationView.getMenu().getItem(5).getSubMenu().getItem(2).setChecked(true);
    }

    @Override
    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        if (campusId == 1) { //Paris
            adapter.addFragment(ClusterMapFragment.newInstance("e1"), "E1");
            adapter.addFragment(ClusterMapFragment.newInstance("e2"), "E2");
            adapter.addFragment(ClusterMapFragment.newInstance("e3"), "E3");
        } else if (campusId == 7) { // Fremont
            adapter.addFragment(ClusterMapFragment.newInstance("e1z1"), "E1Z1");
            adapter.addFragment(ClusterMapFragment.newInstance("e1z2"), "E1Z2");
            adapter.addFragment(ClusterMapFragment.newInstance("e1z3"), "E1Z3");
            adapter.addFragment(ClusterMapFragment.newInstance("e1z4"), "E1Z4");
        }

        viewPager.setAdapter(adapter);

        viewPager.setPageMargin(20);
        viewPager.setPageMarginDrawable(R.color.textColorBlackPrimary);

        if (locationHighlight != null) {
            if (campusId == 1) {
                if (locationHighlight.contains("e1"))
                    viewPager.setCurrentItem(0);
                else if (locationHighlight.contains("e2"))
                    viewPager.setCurrentItem(1);
                else if (locationHighlight.contains("e3"))
                    viewPager.setCurrentItem(2);
            }
        }
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return "https://meta.intra.42.fr/clusters";
    }

    @Override
    public StatusCode getDataOnOtherThread() {

        final List<Locations> locationsTmp = new ArrayList<>();
        campusId = AppSettings.getAppCampus(app);
        if (!(campusId == 1 || campusId == 7))
            return StatusCode.EMPTY;

        setLoadingInfo("Loading locations …");
        setLoadingProgress(0, -1);

        int page = 1;
        int pageSize = 100;
        int pageMax;
        try {
            while (true) {

                Response<List<Locations>> r = app.getApiService().getLocations(campusId, pageSize, page).execute();
                if (r.isSuccessful()) {
                    locationsTmp.addAll(r.body());
                    if (r.body().size() == pageSize) {
                        pageMax = (int) Math.ceil(Double.parseDouble(r.headers().get("X-Total")) / pageSize);
                        setLoadingProgress(page, pageMax);
                        page++;
                    } else
                        break;
                } else
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        locations = new HashMap<>();
        for (Locations l : locationsTmp) {
            locations.put(l.host, l.user);
        }

        return StatusCode.FINISH;
    }

    @Override
    public BasicActivity.StatusCode getDataOnMainThread() {

        List<Campus> campusCache = CacheCampus.get(app.cacheSQLiteHelper);
        if (campusCache != null) {
            for (Campus c : campusCache) {
                if (c.id == 1)
                    campus.add(c);
            }
        }

        return StatusCode.CONTINUE;
    }

    @Override
    public String getToolbarName() {
        return null;
    }

    /**
     * This text is useful when both {@link GetDataOnThread#getDataOnOtherThread()} and {@link BasicActivity.GetDataOnMain#getDataOnMainThread()} return false.
     *
     * @return A simple text to display on screen, may return null;
     */
    @Override
    public String getEmptyText() {
        return "Nothing to show. Not available in your campus";
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
