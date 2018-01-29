package com.paulvarry.intra42.utils.clusterMap;

import android.util.SparseArray;

import com.paulvarry.intra42.activities.clusterMap.ClusterMapActivity;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.api.tools42.FriendsSmall;

import java.util.HashMap;
import java.util.List;

public class ClusterStatus {

    public List<ClusterItem> clusterInfoList;

    /**
     * key : Location name
     * <p>
     * Value : User on this location
     */
    public HashMap<String, UsersLTE> locations;
    public SparseArray<FriendsSmall> friends;

    public ClusterMapActivity.LayerStatus layerStatus = ClusterMapActivity.LayerStatus.FRIENDS;
    public String locationHighlight;
    public String layerLogin;

    public ClusterStatus() {

    }

    public void computeFreeSpots() {
        if (clusterInfoList == null)
            return;
        for (ClusterItem cluster : clusterInfoList) {
            cluster.computeFreePosts(locations);
        }
    }

    public void computeHighlightSpots() {
        if (clusterInfoList == null)
            return;
        for (ClusterItem cluster : clusterInfoList) {
            cluster.computeHighlightPosts(this);
        }
    }
}
