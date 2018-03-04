package com.paulvarry.intra42.activities.clusterMap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ViewStatePagerAdapter;
import com.paulvarry.intra42.api.ApiService42Tools;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.api.model.Campus;
import com.paulvarry.intra42.api.model.Locations;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.api.tools42.Friends;
import com.paulvarry.intra42.api.tools42.FriendsSmall;
import com.paulvarry.intra42.cache.CacheCampus;
import com.paulvarry.intra42.ui.BasicTabActivity;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.ClusterMapContributeUtils;
import com.paulvarry.intra42.utils.Tools;
import com.paulvarry.intra42.utils.clusterMap.ClusterStatus;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Response;

public class ClusterMapActivity
        extends BasicTabActivity
        implements ClusterMapFragment.OnFragmentInteractionListener, BasicThreadActivity.GetDataOnMain, BasicThreadActivity.GetDataOnThread, ClusterMapInfoFragment.OnFragmentInteractionListener {

    final static private String ARG_LOCATION_HIGHLIGHT = "location_highlight";

    List<Campus> campus = new ArrayList<>();
    int campusId;

    ClusterStatus clusterStatus;

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

        clusterStatus = new ClusterStatus();
        clusterStatus.layerStatus = LayerStatus.FRIENDS;
        clusterStatus.layerProjectStatus = ProjectsUsers.Status.IN_PROGRESS;

        Intent i = getIntent();
        if (i != null && i.hasExtra(ARG_LOCATION_HIGHLIGHT)) {
            clusterStatus.layerLocationPost = i.getStringExtra(ARG_LOCATION_HIGHLIGHT);
            clusterStatus.layerStatus = LayerStatus.LOCATION;
        }

        layerTmpLocation = clusterStatus.layerLocationPost;
        layerTmpStatus = clusterStatus.layerStatus;
        layerTmpProjectStatus = clusterStatus.layerProjectStatus;

        dataWrapper = (DataWrapper) getLastCustomNonConfigurationInstance();
        if (dataWrapper != null) {
            clusterStatus = dataWrapper.clusters;
            dataWrapper = null;
        }

        super.setActionBarToggle(ActionBarToggle.HAMBURGER);

        registerGetDataOnOtherThread(this);
        registerGetDataOnMainTread(this);

        campusId = AppSettings.getAppCampus(app);
        super.setSelectedMenu(5, 2);

        super.onCreateFinished();
    }

    @Override
    public void setupViewPager(ViewPager viewPager) {
        ViewStatePagerAdapter adapter = new ViewStatePagerAdapter(getSupportFragmentManager());

        adapter.addFragment(ClusterMapInfoFragment.newInstance(), getString(R.string.title_tab_cluster_map_info));

        if (clusterStatus.clusters != null)
            for (Cluster i : clusterStatus.clusters) {
                adapter.addFragment(ClusterMapFragment.newInstance(i.hostPrefix), i.nameShort);
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
        Gson gson = ServiceGenerator.getGson();
        Type listType = new TypeToken<ArrayList<Cluster>>() {
        }.getType();

        campusId = AppSettings.getAppCampus(app);

        int resId = ClusterMapContributeUtils.getResId(this, campusId);

        if (resId == 0) {
            setViewStateThread(StatusCode.EMPTY);
            return;
        }

        setLoadingProgress(0, -1);
        InputStream ins = getResources().openRawResource(resId);
        String data = Tools.readTextFile(ins);
        List<Cluster> d = gson.fromJson(data, listType);
        clusterStatus.initClusterList(d);

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

        clusterStatus.locations = new HashMap<>();
        for (Locations l : locationsTmp) {
            clusterStatus.locations.put(l.host, l.user);
        }

        setLoadingProgress(R.string.info_loading_friends, pageMax, pageMax + 1);

        ApiService42Tools api = app.getApiService42Tools();
        final List<FriendsSmall> friendsTmp = Friends.getFriends(api);
        setLoadingProgress(pageMax + 1, pageMax + 1);
        clusterStatus.friends = new SparseArray<>();
        for (FriendsSmall f : friendsTmp) {
            clusterStatus.friends.put(f.id, f);
        }

        clusterStatus.computeHighlightAndFreePosts();

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

        if (dataWrapper != null) {
            dataWrapper = null;
            if (clusterStatus.friends != null && clusterStatus.locations != null)
                return ThreadStatusCode.FINISH;
        }

        return ThreadStatusCode.CONTINUE;
    }

    void applyLayerUser(String login) {
        clusterStatus.layerUserLogin = login;
        clusterStatus.layerStatus = LayerStatus.USER;

        clusterStatus.computeHighlightPosts();
        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.invalidate();
    }

    void applyLayerFriends() {
        clusterStatus.layerStatus = LayerStatus.FRIENDS;

        clusterStatus.computeHighlightPosts();
        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.invalidate();
    }

    void applyLayerProject(List<ProjectsUsers> projectsUsers, String slug, ProjectsUsers.Status layerProjectStatus) {
        clusterStatus.layerStatus = LayerStatus.PROJECT;
        clusterStatus.layerProjectSlug = slug;
        clusterStatus.layerProjectStatus = layerProjectStatus;

        if (projectsUsers == null)
            return;
        clusterStatus.projectsUsers = new SparseArray<>();

        for (ProjectsUsers p : projectsUsers) {
            if (p.user != null) {
                clusterStatus.projectsUsers.append(p.user.id, p);
            }
        }

        clusterStatus.computeHighlightPosts();
        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.invalidate();
    }

    void applyLayerProject(ProjectsUsers.Status layerProjectStatus) {
        clusterStatus.layerStatus = LayerStatus.PROJECT;
        clusterStatus.layerProjectStatus = layerProjectStatus;

        clusterStatus.computeHighlightPosts();
        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.invalidate();
    }

    void applyLayerLocation(String location) {
        clusterStatus.layerStatus = LayerStatus.LOCATION;
        clusterStatus.layerLocationPost = location;

        clusterStatus.computeHighlightPosts();
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
        data.clusters = clusterStatus;
        return data;
    }

    void refreshCluster() {
        onCreateFinished();
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
