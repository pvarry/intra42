package com.paulvarry.intra42.utils.clusterMap;

import android.util.SparseArray;

import com.paulvarry.intra42.activities.clusterMap.ClusterMapActivity;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.api.tools42.FriendsSmall;

import java.util.HashMap;

public class ClusterStatus {

    public HashMap<String, ClusterItem> clusterInfoList;

    /**
     * key : Location name
     * <p>
     * Value : User on this location
     */
    public HashMap<String, UsersLTE> locations;
    public SparseArray<FriendsSmall> friends;

    public ClusterMapActivity.LayerStatus layerStatus = ClusterMapActivity.LayerStatus.FRIENDS;
    public String layerUserLogin = "";

    public SparseArray<ProjectsUsers> projectsUsers;
    public String layerProjectSlug = "";
    public ProjectsUsers.Status layerProjectStatus = ProjectsUsers.Status.IN_PROGRESS;
    public String layerLocationPost = "";

    public ClusterStatus() {

    }

    public void addCluster(ClusterItem clusterItem) {
        clusterInfoList.put(clusterItem.hostPrefix, clusterItem);
    }

    public void computeFreePosts() {
        if (clusterInfoList == null)
            return;
        for (ClusterItem cluster : clusterInfoList.values()) {
            cluster.computeFreePosts(locations);
        }
    }

    public void computeHighlightPosts() {
        if (clusterInfoList == null)
            return;
        for (ClusterItem cluster : clusterInfoList.values()) {
            cluster.computeHighlightPosts(this);
        }
    }

    public void computeHighlightAndFreePosts() {
        if (clusterInfoList == null)
            return;
        for (ClusterItem cluster : clusterInfoList.values()) {
            cluster.computeHighlightAndFreePosts(this, locations);
        }
    }

    public UsersLTE getUserInLocation(LocationItem locationItem) {
        if (locationItem.kind == LocationItem.KIND_USER && locationItem.locationName != null && locations != null)
            return locations.get(locationItem.locationName);
        return null;
    }
}
