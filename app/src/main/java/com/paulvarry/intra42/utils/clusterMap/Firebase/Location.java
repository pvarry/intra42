package com.paulvarry.intra42.utils.clusterMap.Firebase;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.utils.clusterMap.LocationItem;

public class Location {

    public String host;
    public Kind locationKind;
    /**
     * between 0 and 1
     * Default 1
     */
    @SerializedName("size_x")
    public float sizeX;
    /**
     * between 0 and 1
     * Default 1
     */
    @SerializedName("size_y")
    public float sizeY;
    public float angle;

    @Deprecated
    public Location(String locationName, int kind, float angle) {

        host = locationName;
        this.locationKind = getKind(kind);
        this.angle = angle;
        this.sizeX = 1f;
        this.sizeY = 1f;
    }

    @Deprecated
    public Location(String locationName, int kind) {

        host = locationName;
        this.locationKind = getKind(kind);
        this.sizeX = 1;
        this.sizeY = 1;
    }

    @Deprecated
    public Location(String locationName, int sizeX, int sizeY, int kind, float angle) {

        host = locationName;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.locationKind = getKind(kind);
        this.angle = angle;
    }

    @Deprecated
    public Kind getKind(int kind) {
        switch (kind) {
            case LocationItem.KIND_USER:
                return Kind.USER;
            case LocationItem.KIND_CORRIDOR:
                return Kind.CORRIDOR;
            case LocationItem.KIND_WALL:
                return Kind.WALL;
            default:
                return Kind.CORRIDOR;
        }
    }

    public enum Kind {
        @SerializedName("user")USER, @SerializedName("corridor")CORRIDOR, @SerializedName("wall")WALL
    }
}
