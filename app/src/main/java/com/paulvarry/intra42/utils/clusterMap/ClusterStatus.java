package com.paulvarry.intra42.utils.clusterMap;

import android.util.SparseArray;

import com.paulvarry.intra42.activities.clusterMap.ClusterMapActivity;
import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.api.cluster_map_contribute.Location;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.api.tools42.FriendsSmall;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClusterStatus {

    public HashMap<String, Cluster> clusterInfoList;
    public Map<String, ClusterItem> clusterInfoList;

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

    public void addCluster(Cluster clusterItem) {
        clusterInfoList.put(clusterItem.hostPrefix, clusterItem);
    }

    public void addCluster(List<Cluster> cluster) {
        if (cluster == null) return;
        for (Cluster i : cluster)
            addCluster(i);
    }

    public void computeFreePosts() {
        if (clusterInfoList == null)
            return;
        for (Cluster cluster : clusterInfoList.values()) {
            cluster.computeFreePosts(locations);
        }
    }

    public void computeHighlightPosts() {
        if (clusterInfoList == null)
            return;
        for (Cluster cluster : clusterInfoList.values()) {
            cluster.computeHighlightPosts(this);
        }
    }

    public UsersLTE getUserInLocation(Location locationItem) {
        if (locationItem.kind == Location.Kind.USER &&
                locationItem.host != null &&
                !locationItem.host.isEmpty() &&
                locations != null)
            return locations.get(locationItem.host);
        return null;
    }

    public void computeHighlightAndFreePosts() {
        if (clusterInfoList == null)
            return;
        for (Cluster cluster : clusterInfoList.values()) {
            cluster.computeHighlightAndFreePosts(this, locations);
        }
    }
}
