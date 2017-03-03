package com.paulvarry.intra42.tab.clusterMap;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import com.paulvarry.intra42.Adapter.ViewPagerAdapter;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.AppSettings;
import com.paulvarry.intra42.api.Campus;
import com.paulvarry.intra42.api.Locations;
import com.paulvarry.intra42.api.UserLTE;
import com.paulvarry.intra42.cache.CacheCampus;
import com.paulvarry.intra42.ui.BasicActivity;
import com.paulvarry.intra42.ui.BasicTabActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Response;

public class ClusterMapActivity extends BasicTabActivity implements ClusterMapFragment.OnFragmentInteractionListener {

    HashMap<String, UserLTE> locations;
    List<Campus> campus = new ArrayList<>();
    int campusId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.activeHamburger();

        super.onCreate(savedInstanceState);
        campusId = AppSettings.getUserCampus(app);
        navigationView.getMenu().getItem(5).getSubMenu().getItem(1).setChecked(true);
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return "https://meta.intra.42.fr/clusters";
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
    }

    @Override
    public boolean getDataOnOtherThread() {

        final List<Locations> locationsTmp = new ArrayList<>();
        campusId = AppSettings.getAppCampus(app);
        if (!(campusId == 1 || campusId == 7))
            return false;

        int page = 1;
        int pageSize = 100;
        try {
            while (true) {

                Response<List<Locations>> r = app.getApiService().getLocations(campusId, pageSize, page).execute();
                if (r.isSuccessful()) {
                    locationsTmp.addAll(r.body());
                    if (r.body().size() == pageSize) {
                        page++;
                        final int finalPage = page;
                        setLoadingStatus("page " + String.valueOf(finalPage));
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

        return true;
    }

    @Override
    public boolean getDataOnMainThread() {

        List<Campus> campusCache = CacheCampus.get(app.cacheSQLiteHelper);
        if (campusCache != null) {
            for (Campus c : campusCache) {
                if (c.id == 1)
                    campus.add(c);
            }
        }

        return false;
    }

    @Override
    public String getToolbarName() {
        return null;
    }

    /**
     * This text is useful when both {@link BasicActivity#getDataOnMainThread()} and {@link BasicActivity#getDataOnOtherThread()} return false.
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
