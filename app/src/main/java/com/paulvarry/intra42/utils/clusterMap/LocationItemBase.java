package com.paulvarry.intra42.utils.clusterMap;

public class LocationItemBase {

    public final static int KIND_USER = 0;
    public final static int KIND_CORRIDOR = 1;
    public final static int KIND_WALL = 2;
    /**
     * 0: user emplacement;
     * 1: corridor;
     * 2: wall;
     */
    public float kind;
    /**
     * 0 -> 1
     * Default 1
     */
    public float sizeX;
    /**
     * 0 -> 1
     * Default 1
     */
    public float sizeY;
    public String locationName;
    float angle;

    public LocationItemBase(String locationName, int kind, float angle) {

        this.locationName = locationName;
        this.kind = kind;
        this.angle = angle;
        this.sizeX = 1f;
        this.sizeY = 1f;
    }

    public LocationItemBase(String locationName, int kind) {

        this.locationName = locationName;
        this.kind = kind;
        this.sizeX = 1;
        this.sizeY = 1;
    }

    public LocationItemBase(String locationName, int sizeX, int sizeY, int kind, float angle) {

        this.locationName = locationName;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.kind = kind;
        this.angle = angle;
    }
}