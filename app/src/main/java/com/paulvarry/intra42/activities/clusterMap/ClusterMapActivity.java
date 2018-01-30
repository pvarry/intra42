package com.paulvarry.intra42.activities.clusterMap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ViewStatePagerAdapter;
import com.paulvarry.intra42.api.ApiService42Tools;
import com.paulvarry.intra42.api.model.Campus;
import com.paulvarry.intra42.api.model.Locations;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.api.tools42.FriendsSmall;
import com.paulvarry.intra42.cache.CacheCampus;
import com.paulvarry.intra42.ui.BasicTabActivity;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.Tools;
import com.paulvarry.intra42.utils.clusterMap.ClusterItem;
import com.paulvarry.intra42.utils.clusterMap.ClusterStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ClusterMapActivity
        extends BasicTabActivity
        implements ClusterMapFragment.OnFragmentInteractionListener, BasicThreadActivity.GetDataOnMain, BasicThreadActivity.GetDataOnThread, ClusterMapInfoFragment.OnFragmentInteractionListener {

    final static private String ARG_LOCATION_HIGHLIGHT = "location_highlight";

    List<Campus> campus = new ArrayList<>();
    int campusId;

    ClusterStatus clusters;

    DataWrapper dataWrapper;

    ClusterMapActivity.LayerStatus layerTmpStatus;
    String layerTmpLogin = "";
    String layerTmpProjectSlug = "";
    String layerTmpLocation = "";

    ProjectsUsers.Status layerTmpProjectStatus;

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
        super.onCreate(savedInstanceState);

        clusters = new ClusterStatus();
        clusters.layerStatus = LayerStatus.FRIENDS;
        clusters.layerProjectStatus = ProjectsUsers.Status.IN_PROGRESS;

        Intent i = getIntent();
        if (i != null && i.hasExtra(ARG_LOCATION_HIGHLIGHT)) {
            clusters.layerLocationPost = i.getStringExtra(ARG_LOCATION_HIGHLIGHT);
            clusters.layerStatus = LayerStatus.LOCATION;
        }

        layerTmpLocation = clusters.layerLocationPost;
        layerTmpStatus = clusters.layerStatus;
        layerTmpProjectStatus = clusters.layerProjectStatus;

        dataWrapper = (DataWrapper) getLastCustomNonConfigurationInstance();
        if (dataWrapper != null) {
            clusters = dataWrapper.clusters;
            dataWrapper = null;
        }

        super.setActionBarToggle(ActionBarToggle.HAMBURGER);

        registerGetDataOnOtherThread(this);
        registerGetDataOnMainTread(this);

        campusId = AppSettings.getAppCampus(app);
        navigationView.getMenu().getItem(5).getSubMenu().getItem(2).setChecked(true);

        super.onCreateFinished();
    }

    @Override
    public void setupViewPager(ViewPager viewPager) {
        ViewStatePagerAdapter adapter = new ViewStatePagerAdapter(getSupportFragmentManager());

        adapter.addFragment(ClusterMapInfoFragment.newInstance(), getString(R.string.title_tab_cluster_map_info));

        for (ClusterItem i : clusters.clusterInfoList.values()) {
            adapter.addFragment(ClusterMapFragment.newInstance(i.hostPrefix), i.name);
        }

        viewPager.setAdapter(adapter);

        viewPager.setPageMargin(20);
        viewPager.setPageMarginDrawable(R.color.textColorBlackPrimary);
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return getString(R.string.base_url_intra_cluster_map);
    }

    @Override
    public void getDataOnOtherThread() throws IOException {

        final List<Locations> locationsTmp = new ArrayList<>();

        campusId = AppSettings.getAppCampus(app);
        clusters.clusterInfoList = new HashMap<>();
        if (campusId == 1) { //Paris
            clusters.addCluster(new ClusterItem(campusId, "E1", "e1"));
            clusters.addCluster(new ClusterItem(campusId, "E2", "e2"));
            clusters.addCluster(new ClusterItem(campusId, "E3", "e3"));
        } else if (campusId == 7) { // Fremont
            clusters.addCluster(new ClusterItem(campusId, "E1Z1", "e1z1"));
            clusters.addCluster(new ClusterItem(campusId, "E1Z2", "e1z2"));
            clusters.addCluster(new ClusterItem(campusId, "E1Z3", "e1z3"));
            clusters.addCluster(new ClusterItem(campusId, "E1Z4", "e1z4"));
        } else {
            setViewStateThread(StatusCode.EMPTY);
            return;
        }

        setLoadingProgress(R.string.info_loading_locations, 0, -1);

        int page = 1;
        int pageSize = 100;
        int pageMax = 0;

        while (true) {

            Response<List<Locations>> r = app.getApiService().getLocations(campusId, pageSize, page).execute();
            if (Tools.apiIsSuccessful(r)) {
                locationsTmp.addAll(r.body());
                if (r.body().size() == pageSize) {
                    pageMax = (int) Math.ceil(Double.parseDouble(r.headers().get("X-Total")) / pageSize);
                    setLoadingProgress(page, pageMax + 1);
                    page++;
                } else
                    break;
            } else {
                setViewStateThread(StatusCode.API_DATA_ERROR);
                return;
            }
        }

        clusters.locations = new HashMap<>();
        for (Locations l : locationsTmp) {
            clusters.locations.put(l.host, l.user);
        }

        clusters.computeFreePosts();

        setLoadingProgress(R.string.info_loading_friends, pageMax, pageMax + 1);

        ApiService42Tools api = app.getApiService42Tools();

        Call<List<FriendsSmall>> call = api.getFriends();
        Response<List<FriendsSmall>> ret = call.execute();
        if (Tools.apiIsSuccessful(ret)) {
            clusters.friends = new SparseArray<>();
            for (FriendsSmall f : ret.body()) {
                clusters.friends.put(f.id, f);
            }
        }

        clusters.computeHighlightPosts();

        setViewStateThread(StatusCode.CONTENT);
    }

    @Override
    public ThreadStatusCode getDataOnMainThread() {

        List<Campus> campusCache = CacheCampus.get(app.cacheSQLiteHelper);
        if (campusCache != null) {
            for (Campus c : campusCache) {
                if (c.id == 1)
                    campus.add(c);
            }
        }

        if (clusters.friends != null && clusters.locations != null)
            return ThreadStatusCode.FINISH;

        return ThreadStatusCode.CONTINUE;
    }

    void applyLayerUser(String login) {
        clusters.layerUserLogin = login;
        clusters.layerStatus = LayerStatus.USER;

        clusters.computeHighlightPosts();
        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.invalidate();
    }

    void applyLayerFriends() {
        clusters.layerStatus = LayerStatus.FRIENDS;

        clusters.computeHighlightPosts();
        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.invalidate();
    }

    void applyLayerProject(List<ProjectsUsers> projectsUsers, String slug, ProjectsUsers.Status layerProjectStatus) {
        clusters.layerStatus = LayerStatus.PROJECT;
        clusters.layerProjectSlug = slug;
        clusters.layerProjectStatus = layerProjectStatus;

        if (projectsUsers == null)
            return;
        clusters.projectsUsers = new SparseArray<>();

        for (ProjectsUsers p : projectsUsers) {
            if (p.user != null) {
                clusters.projectsUsers.append(p.user.id, p);
            }
        }

        clusters.computeHighlightPosts();
        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.invalidate();
    }

    void applyLayerProject(ProjectsUsers.Status layerProjectStatus) {
        clusters.layerStatus = LayerStatus.PROJECT;
        clusters.layerProjectStatus = layerProjectStatus;

        clusters.computeHighlightPosts();
        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.invalidate();
    }

    void applyLayerLocation(String location) {
        clusters.layerStatus = LayerStatus.LOCATION;
        clusters.layerLocationPost = location;

        clusters.computeHighlightPosts();
        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.invalidate();
    }

    @Override
    public String getToolbarName() {
        return null;
    }

    /**
     * This text is useful when both {@link GetDataOnThread#getDataOnOtherThread()} and {@link BasicThreadActivity.GetDataOnMain#getDataOnMainThread()} return false.
     *
     * @return A simple text to display on screen, may return null;
     */
    @Override
    public String getEmptyText() {
        return getString(R.string.cluster_map_not_in_campus);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public final Object onRetainCustomNonConfigurationInstance() {
        DataWrapper data = new DataWrapper();
        data.clusters = clusters;
        return data;
    }

    public enum LayerStatus {
        FRIENDS(0), PROJECT(1), USER(2), LOCATION(3);

        private final int id;

        LayerStatus(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    private class DataWrapper {
        ClusterStatus clusters;
    }


}
