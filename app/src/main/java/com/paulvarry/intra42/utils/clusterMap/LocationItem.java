package com.paulvarry.intra42.utils.clusterMap;

import android.support.annotation.Nullable;

import com.paulvarry.intra42.activities.clusterMap.ClusterMapActivity;
import com.paulvarry.intra42.api.model.UsersLTE;

public class LocationItem extends LocationItemBase {

    @Nullable
    public Boolean highlight;

    public LocationItem(String locationName, int kind, float angle) {
        super(locationName, kind, angle);
    }

    public LocationItem(String locationName, int kind) {
        super(locationName, kind);
    }

    public LocationItem(String locationName, int sizeX, int sizeY, int kind, float angle) {
        super(locationName, sizeX, sizeY, kind, angle);
    }

    public boolean computeHighlightPosts(ClusterStatus cluster, UsersLTE user) {
        boolean highlight = false;

        if (cluster != null && user != null)
            if (cluster.layerStatus == ClusterMapActivity.LayerStatus.FRIENDS) {
                if (cluster.friends != null && cluster.friends.get(user.id) != null)
                    highlight = true;
            } else if (cluster.layerStatus == ClusterMapActivity.LayerStatus.USER_HIGHLIGHT) {
                if (cluster.layerLogin.contentEquals(user.login))
                    highlight = true;
            }

        this.highlight = highlight;
        return highlight;
    }

}