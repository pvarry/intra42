package com.paulvarry.intra42.utils.clusterMap.Firebase;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.BaseItem;

public class Cluster implements BaseItem {

    public String name;
    public String slug;
    @SerializedName("host_prefix")
    public String hostPrefix;
    @SerializedName("campus_id")
    public int campusId;
    @SerializedName("position")
    public int clusterPosition;
    @SerializedName("size_x")
    public int sizeX;
    @SerializedName("size_y")
    public int sizeY;
    public Location map[][];

    public Cluster(int campusId, String name, String hostPrefix) {
        this.campusId = campusId;
        this.name = name;
        this.hostPrefix = hostPrefix;
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
