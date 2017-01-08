package com.paulvarry.intra42.tab.clusterMap;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.paulvarry.intra42.Adapter.ViewPagerAdapter;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.Campus;
import com.paulvarry.intra42.api.Locations;
import com.paulvarry.intra42.api.UserLTE;
import com.paulvarry.intra42.cache.CacheCampus;
import com.paulvarry.intra42.ui.BasicTabActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Response;

public class ClusterMapActivity extends BasicTabActivity implements ClusterMapFragment.OnFragmentInteractionListener {

    TextView textViewStatus;

    HashMap<String, UserLTE> locations;
    List<Campus> campus = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.allowHamburger();

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return "https://meta.intra.42.fr/clusters";
    }

    @Override
    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        if (true) //Paris
        {
            adapter.addFragment(ClusterMapFragment.newInstance("e1"), "E1");
            adapter.addFragment(ClusterMapFragment.newInstance("e2"), "E2");
            adapter.addFragment(ClusterMapFragment.newInstance("e3"), "E3");
        }
        viewPager.setAdapter(adapter);

        viewPager.setPageMargin(20);
        viewPager.setPageMarginDrawable(R.color.textColorBlackPrimary);
    }

    @Override
    public boolean getDataOnOtherThread() {

        final List<Locations> locationsTmp = new ArrayList<>();

        int page = 1;
        int pageSize = 100;
        try {
            while (true) {

                Response<List<Locations>> r = app.getApiService().getLocations(1, pageSize, page).execute();
                if (r.isSuccessful()) {
                    locationsTmp.addAll(r.body());
                    if (r.body().size() == pageSize) {
                        page++;
                        final int finalPage = page;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                textViewStatus.setText("page " + String.valueOf(finalPage));
                            }
                        });
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
