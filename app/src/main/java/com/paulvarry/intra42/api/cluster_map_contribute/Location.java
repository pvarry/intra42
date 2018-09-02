package com.paulvarry.intra42.api.cluster_map_contribute;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.model.CursusUsers;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.clusterMap.ClusterData;
import com.paulvarry.intra42.utils.clusterMap.ClusterLayersSettings;

import java.io.Serializable;
import java.util.Date;

import androidx.annotation.Nullable;

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

    public transient boolean highlight = false;

    public Location() {
        sizeX = 1;
        sizeY = 1;
    }

    boolean computeHighlightPosts(ClusterData clusterData, ClusterLayersSettings layersSettings, UsersLTE user) {
        if (clusterData != null && user != null)
            switch (layersSettings.layer) {
                case FRIENDS:
                    if (clusterData.friends != null && clusterData.friends.get(user.id) != null)
                        highlight = true;
                    break;
                case USER:
                    if (layersSettings.layerUserLogin.contentEquals(user.login))
                        highlight = true;
                    break;
                case PROJECT:
                    ProjectsUsers projectsUsers;
                    if (clusterData.projectsUsers != null && (projectsUsers = clusterData.projectsUsers.get(user.id)) != null && projectsUsers.status == layersSettings.layerProjectStatus)
                        highlight = true;
                    break;
                case LOCATION:
                    if (host == null)
                        highlight = false;
                    else
                        highlight = (layersSettings.layerLocationPost.contentEquals(host));
                    break;
                case LEVEL:
                    highlight = false;
                    CursusUsers cursusUser = clusterData.cursusUsers.get(layersSettings.layerLevelCursus).get(user.id);
                    if (cursusUser == null)
                        break;
                    if (!layersSettings.useClosedCursusUser &&
                            (cursusUser.end_at == null || cursusUser.end_at.after(new Date())))
                        break;
                    float level = cursusUser.level;
                    if (layersSettings.layerLevelMax == -1f) {
                        highlight = layersSettings.layerLevelMin <= level;
                    } else {
                        highlight = layersSettings.layerLevelMin <= level && layersSettings.layerLevelMax > level;
                    }
                    break;
            }

        return highlight;
    }

    public enum Kind implements Serializable {
        @SerializedName("user") USER, @SerializedName("corridor") CORRIDOR, @SerializedName("wall") WALL
    }
}
