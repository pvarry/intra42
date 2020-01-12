package com.paulvarry.intra42.api.cluster_map;

import android.util.SparseArray;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.model.CursusUsers;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.clusterMap.ClusterData;
import com.paulvarry.intra42.utils.clusterMap.ClusterLayersSettings;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Location implements Serializable {

    private final static String FIELD_HOST = "host";
    private final static String FIELD_KIND = "kind";
    private final static String FIELD_SIZE_X = "sizeX";
    private final static String FIELD_SIZE_Y = "sizeY";
    private final static String FIELD_ANGLE = "angle";
    @Nullable
    @SerializedName(FIELD_HOST)
    public String host;
    @Nullable
    @SerializedName(FIELD_KIND)
    public Kind kind;
    /**
     * between 0 and 1
     * Default 1
     */
    @SerializedName(FIELD_SIZE_X)
    public float sizeX = 1;
    /**
     * between 0 and 1
     * Default 1
     */
    @SerializedName(FIELD_SIZE_Y)
    public float sizeY = 1;
    @SerializedName(FIELD_ANGLE)
    public float angle;
    public transient boolean highlight = false;

    public Location(Map<String, Object> map) {
        host = (String) map.get(FIELD_HOST);
        if (map.get(FIELD_KIND) != null)
            kind = Kind.valueOf((String) map.get(FIELD_KIND));

        Object x = map.get(FIELD_SIZE_X);
        if (x != null)
            sizeX = Float.parseFloat(x.toString());
        Object y = map.get(FIELD_SIZE_Y);
        if (y != null)
            sizeY = Float.parseFloat(y.toString());
        Object a = map.get(FIELD_ANGLE);
        if (a != null)
            angle = Float.parseFloat(a.toString());
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

    public Map<String, Object> export() {
        Map<String, Object> item = new HashMap<>();
        item.put(FIELD_HOST, host);
        if (kind != null)
            item.put(FIELD_KIND, kind.name().toUpperCase());
        item.put(FIELD_SIZE_X, sizeX);
        item.put(FIELD_SIZE_Y, sizeY);
        item.put(FIELD_ANGLE, angle);

        return item;
    }

    public enum Kind implements Serializable {
        @SerializedName("USER") USER, @SerializedName("CORRIDOR") CORRIDOR, @SerializedName("WALL") WALL
    }

}
