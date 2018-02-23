package com.paulvarry.intra42.api.cluster_map_contribute;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.BaseItem;
import com.paulvarry.intra42.utils.clusterMap.ClusterMapGenerator;

import java.io.Serializable;

public class Cluster implements BaseItem, Serializable {

    public String name;
    public String nameShort;
    public String slug;
    @SerializedName("host_prefix")
    public String hostPrefix;
    @SerializedName("campus_id")
    public int campusId;
    @SerializedName("position")
    public int clusterPosition;
    @SerializedName("width")
    public int sizeX;
    @SerializedName("height")
    public int sizeY;
    public Location map[][];

    public Cluster(int campusId, String name, String hostPrefix) {
        this.campusId = campusId;
        this.name = name;
        this.nameShort = name;
        this.hostPrefix = hostPrefix;
    }

    public Cluster(int campusId, String name, String hostPrefix, boolean generateMap) {
        this.campusId = campusId;
        this.name = name;
        this.nameShort = name;
        this.hostPrefix = hostPrefix;
        if (generateMap)
            map = ClusterMapGenerator.getClusterMap(campusId, hostPrefix);
    }

    @Override
    public String getName(Context context) {
        return name;
    }

    @Override
    public String getSub(Context context) {
        return slug;
    }

    @Override
    public boolean openIt(Context context) {
        return false;
    }
}
