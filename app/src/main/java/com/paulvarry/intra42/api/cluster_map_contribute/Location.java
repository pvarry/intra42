package com.paulvarry.intra42.api.cluster_map_contribute;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.clusterMap.ClusterStatus;

import java.io.Serializable;

public class Location implements Serializable {

    @Nullable
    @SerializedName("host")
    public String host;
    @Nullable
    @SerializedName("kind")
    public Kind kind;
    /**
     * between 0 and 1
     * Default 1
     */
    @SerializedName("scale_x")
    public float sizeX;
    /**
     * between 0 and 1
     * Default 1
     */
    @SerializedName("scale_y")
    public float sizeY;
    @SerializedName("rot")
    public float angle;

    @Nullable
    public transient Boolean highlight;

    public Location() {
        sizeX = 1;
        sizeY = 1;
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
                    highlight = (cluster.layerLocationPost.contentEquals(host));
                    break;
            }

        this.highlight = highlight;
        return highlight;
    }

    public enum Kind implements Serializable {
        @SerializedName("user")USER, @SerializedName("corridor")CORRIDOR, @SerializedName("wall")WALL
    }
}
