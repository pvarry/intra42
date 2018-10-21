package com.paulvarry.intra42.api.cluster_map;

import android.util.SparseArray;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.model.CursusUsers;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.clusterMap.ClusterData;
import com.paulvarry.intra42.utils.clusterMap.ClusterLayersSettings;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

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
    @SerializedName("sizeX")
    public float sizeX;
    /**
     * between 0 and 1
     * Default 1
     */
    @SerializedName("sizeY")
    public float sizeY;
    @SerializedName("angle")
    public float angle;

    public transient boolean highlight = false;

    public Location(Map<String, Object> map) {
        host = (String) map.get("host");
        if (map.get("kind") != null)
            kind = Kind.valueOf((String) map.get("kind"));
        sizeX = Float.valueOf(map.get("sizeX").toString());
        sizeY = Float.valueOf(map.get("sizeY").toString());
        angle = Float.valueOf(map.get("angle").toString());
    }

    public Location() {
        sizeX = 1;
        sizeY = 1;
    }

    boolean computeHighlightPosts(ClusterData clusterData, ClusterLayersSettings layersSettings, UsersLTE user) {
        highlight = false;
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
                    if (clusterData.projectsUsers != null &&
                            (projectsUsers = clusterData.projectsUsers.get(user.id)) != null &&
                            projectsUsers.status == layersSettings.layerProjectStatus)
                        highlight = true;
                    break;
                case LOCATION:
                    if (host != null && (layersSettings.layerLocationPost.contentEquals(host)))
                        highlight = true;
                    break;
                case LEVEL:
                    if (clusterData.cursusUsers == null) break;
                    SparseArray<CursusUsers> cursusUsersArray = clusterData.cursusUsers.get(layersSettings.layerLevelCursus);
                    if (cursusUsersArray == null) break;
                    CursusUsers cursusUser = cursusUsersArray.get(user.id);
                    if (cursusUser == null) break;
                    if (!layersSettings.layerLevelShowClosedCursusUser &&
                            cursusUser.end_at != null && cursusUser.end_at.before(new Date()))
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
        @SerializedName("USER") USER, @SerializedName("CORRIDOR") CORRIDOR, @SerializedName("WALL") WALL
    }
}
