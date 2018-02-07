package com.paulvarry.intra42.utils.clusterMap;

import android.support.annotation.Nullable;

import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.clusterMap.Firebase.Location;

public class LocationItem extends Location {

    public final static int KIND_USER = 0;
    public final static int KIND_CORRIDOR = 1;
    public final static int KIND_WALL = 2;

    @Nullable
    public Boolean highlight;
    public String locationName;
    public int kind;

    public LocationItem(String locationName, int kind, float angle) {
        super(locationName, kind, angle);
        this.kind = kind;
        this.locationName = locationName;
    }

    public LocationItem(String locationName, int kind) {
        super(locationName, kind);
        this.kind = kind;
        this.locationName = locationName;
    }

    public LocationItem(String locationName, int sizeX, int sizeY, int kind, float angle) {
        super(locationName, sizeX, sizeY, kind, angle);
        this.kind = kind;
        this.locationName = locationName;
    }

    public boolean computeHighlightPosts(ClusterStatus cluster, UsersLTE user) {
        boolean highlight = false;

        if (cluster != null && user != null)
            switch (cluster.layerStatus) {
                case FRIENDS:
                    if (cluster.friends != null && cluster.friends.get(user.id) != null)
                        highlight = true;
                    break;
                case USER:
                    if (cluster.layerUserLogin.contentEquals(user.login))
                        highlight = true;
                    break;
                case PROJECT:
                    ProjectsUsers projectsUsers;
                    if (cluster.projectsUsers != null && (projectsUsers = cluster.projectsUsers.get(user.id)) != null && projectsUsers.status == cluster.layerProjectStatus)
                        highlight = true;
                    break;
                case LOCATION:
                    highlight = (cluster.layerLocationPost.contentEquals(locationName));
                    break;
            }

        this.highlight = highlight;
        return highlight;
    }

}