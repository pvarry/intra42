package com.paulvarry.intra42.activities.clusterMap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ViewStatePagerAdapter;
import com.paulvarry.intra42.api.ApiService42Tools;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.api.model.CursusUsers;
import com.paulvarry.intra42.api.model.Locations;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.api.tools42.Friends;
import com.paulvarry.intra42.api.tools42.FriendsSmall;
import com.paulvarry.intra42.cache.CacheCursus;
import com.paulvarry.intra42.ui.BasicTabActivity;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.ui.CustomViewPager;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.ClusterMapContributeUtils;
import com.paulvarry.intra42.utils.Tools;
import com.paulvarry.intra42.utils.clusterMap.ClusterData;
import com.paulvarry.intra42.utils.clusterMap.ClusterLayersSettings;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import retrofit2.Response;

public class ClusterMapActivity
        extends BasicTabActivity
        implements ClusterMapFragment.OnFragmentInteractionListener, BasicThreadActivity.GetDataOnMain, BasicThreadActivity.GetDataOnThread, ClusterMapInfoFragment.OnFragmentInteractionListener {

    final static private String ARG_LOCATION_HIGHLIGHT = "location_highlight";
    public List<ClusterLayersSettings.LayerStatus> haveErrorOnLayer = new ArrayList<>();

    ClusterData clusterData;
    ClusterLayersSettings layerSettings;
    ClusterLayersSettings layerSettingsInProgress;

    private int campusId;
    private DataWrapper dataWrapper;

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

        dataWrapper = (DataWrapper) getLastCustomNonConfigurationInstance();
        if (dataWrapper != null && dataWrapper.clusters != null) {
            clusterData = dataWrapper.clusters;
        } else {
            clusterData = new ClusterData();
            layerSettings = new ClusterLayersSettings();
            layerSettings.layer = ClusterLayersSettings.LayerStatus.FRIENDS;
            layerSettings.layerProjectStatus = ProjectsUsers.Status.IN_PROGRESS;
            layerSettings.layerLevelCursus = AppSettings.getAppCursus(app);
            dataWrapper = null;

            Intent i = getIntent();
            if (i != null && i.hasExtra(ARG_LOCATION_HIGHLIGHT)) {
                layerSettings.layerLocationPost = i.getStringExtra(ARG_LOCATION_HIGHLIGHT);
                layerSettings.layer = ClusterLayersSettings.LayerStatus.LOCATION;
            }
        }

        layerSettingsInProgress = new ClusterLayersSettings(layerSettings);

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

        if (clusterData.clusters != null)
            for (Cluster i : clusterData.clusters) {
                adapter.addFragment(ClusterMapFragment.newInstance(i.hostPrefix), i.nameShort);
            }

        viewPager.setAdapter(adapter);

        viewPager.setPageMargin(20);
        viewPager.setPageMarginDrawable(R.color.textColorBlackPrimary);
        ((CustomViewPager) viewPager).setPagingEnabled(false);
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return getString(R.string.base_url_intra_cluster_map);
    }

    @Override
    public void getDataOnOtherThread() throws IOException {

        final List<Locations> locationsTmp = new ArrayList<>();


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

        clusterData.cursusList = CacheCursus.getAllowInternet(app.cacheSQLiteHelper, app); //init cache for cursus

        clusterData.locations = new HashMap<>();
        for (Locations l : locationsTmp) {
            clusterData.locations.put(l.host, l.user);
        }

        setLoadingProgress(R.string.info_loading_friends, pageMax, pageMax + 1);

        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.fetch(AppClass.FIREBASE_REMOTE_CONFIG_CACHE_EXPIRATION);
        try {
            ApiService42Tools api = app.getApiService42Tools();
            final List<FriendsSmall> friendsTmp = Friends.getFriends(api);
            setLoadingProgress(pageMax + 1, pageMax + 1);
            clusterData.friends = new SparseArray<>();
            for (FriendsSmall f : friendsTmp) {
                clusterData.friends.put(f.id, f);
            }
            haveErrorOnLayer.remove(ClusterLayersSettings.LayerStatus.FRIENDS);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            if (!haveErrorOnLayer.contains(ClusterLayersSettings.LayerStatus.FRIENDS)) {
                haveErrorOnLayer.add(ClusterLayersSettings.LayerStatus.FRIENDS);
            }
        }

        clusterData.computeHighlightAndFreePosts(layerSettings);

        setViewStateThread(StatusCode.CONTENT);
    }

    @Override
    public ThreadStatusCode getDataOnMainThread() {

        if (dataWrapper != null) {
            dataWrapper = null;
            if (clusterData.friends != null && clusterData.locations != null)
                return ThreadStatusCode.FINISH;
        }

        Gson gson = ServiceGenerator.getGson();
        Type listType = new TypeToken<ArrayList<Cluster>>() {
        }.getType();

        campusId = AppSettings.getAppCampus(app);

        int resId = ClusterMapContributeUtils.getResId(this, campusId);

        if (resId == 0) {
            setViewStateThread(StatusCode.EMPTY);
            return ThreadStatusCode.NONE;
        }

        setLoadingProgress(0, -1);
        InputStream ins = getResources().openRawResource(resId);
        String data = Tools.readTextFile(ins);
        List<Cluster> clusterList = gson.fromJson(data, listType);
        clusterData.initClusterList(clusterList);

        return ThreadStatusCode.CONTINUE;
    }

    void updateView() {
        clusterData.computeHighlightPosts(layerSettings);
        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.invalidate();
    }

    void removeLayer() {
        layerSettings.layer = ClusterLayersSettings.LayerStatus.NONE;

        updateView();
    }

    void applyLayerUser(String login) {
        layerSettings.layerUserLogin = login;
        layerSettings.layer = ClusterLayersSettings.LayerStatus.USER;

        updateView();
    }

    void applyLayerFriends() {
        layerSettings.layer = ClusterLayersSettings.LayerStatus.FRIENDS;

        updateView();
    }

    void applyLayerProject(List<ProjectsUsers> projectsUsers, String slug, ProjectsUsers.Status layerProjectStatus) {
        layerSettings.layer = ClusterLayersSettings.LayerStatus.PROJECT;
        layerSettings.layerProjectSlug = slug;
        layerSettings.layerProjectStatus = layerProjectStatus;

        if (projectsUsers == null)
            return;
        clusterData.projectsUsers = new SparseArray<>();

        for (ProjectsUsers p : projectsUsers) {
            if (p.user != null) {
                clusterData.projectsUsers.append(p.user.id, p);
            }
        }

        updateView();
    }

    void applyLayerProject(ProjectsUsers.Status layerProjectStatus) {
        layerSettings.layer = ClusterLayersSettings.LayerStatus.PROJECT;
        layerSettings.layerProjectStatus = layerProjectStatus;

        updateView();
    }

    void applyLayerLocation(String location) {
        layerSettings.layer = ClusterLayersSettings.LayerStatus.LOCATION;
        layerSettings.layerLocationPost = location;

        updateView();
    }

    public void applyLayerLevel(List<CursusUsers> cursusUsersList) {
        layerSettings.layer = ClusterLayersSettings.LayerStatus.LEVEL;
        layerSettings.layerLevelMin = layerSettingsInProgress.layerLevelMin;
        layerSettings.layerLevelMax = layerSettingsInProgress.layerLevelMax;
        layerSettings.layerLevelCursus = layerSettingsInProgress.layerLevelCursus;

        if (cursusUsersList == null)
            return;
        clusterData.cursusUsers = new SparseArray<>();

        for (CursusUsers c : cursusUsersList) {
            if (c.user != null) {
                if (clusterData.cursusUsers.get(c.cursusId) == null)
                    clusterData.cursusUsers.append(c.cursusId, new SparseArray<CursusUsers>());
                clusterData.cursusUsers.get(c.cursusId).append(c.user.id, c);
            }
        }

        updateView();
    }

    public void applyLayerLevel() {
        layerSettings.layer = ClusterLayersSettings.LayerStatus.LEVEL;
        layerSettings.layerLevelMin = layerSettingsInProgress.layerLevelMin;
        layerSettings.layerLevelMax = layerSettingsInProgress.layerLevelMax;
        layerSettings.layerLevelCursus = layerSettingsInProgress.layerLevelCursus;

        updateView();
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
        data.clusters = clusterData;
        return data;
    }

    void refreshCluster() {
        clusterData.cursusUsers = null;
        clusterData.projectsUsers = null;
        clusterData.friends = null;
        onCreateFinished();
    }

    private class DataWrapper {
        ClusterData clusters;
    }
}
