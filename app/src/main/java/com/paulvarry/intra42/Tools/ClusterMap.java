package com.paulvarry.intra42.Tools;

public class ClusterMap {

    public static LocationItem[][] getParisCluster(String clusterID) {

        final int cluster[][] = {
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0}, //r13
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //r12
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //r11
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //r10
                {0, 0, 0, 0, 0, 0, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 2, 1, 0, 0, 0, 0, 0, 0, 0}, //r9
                {0, 0, 0, 0, 0, 0, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //r8
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //r7
                {0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 0, 0, 0, 0, 0, 0}, //r6
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0}, //r5
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //r4
                {0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 0, 0, 2, 1, 0, 0, 0, 0, 0, 0, 0}, //r3
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //r2
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0}  //r1
        };

        LocationItem map[][] = new LocationItem[cluster.length][cluster[0].length];

        for (int r = 0; r < cluster.length; r++) {
            int realP = 0;

            for (int p = 0; p < cluster[r].length; p++) {

                int locationKind = cluster[r][p];
                String locationName = null;
                if (locationKind == 0) {
                    realP++;
                    locationName = clusterID + "r" + String.valueOf(13 - r) + "p" + String.valueOf(realP);
                }

                map[r][p] = new LocationItem(locationName, locationKind);
            }
        }

        for (int r = 0; r < cluster.length; r++) {
            map[r][7].sizeX = (float) 0.5;
            map[r][17].sizeX = (float) 0.5;
        }

        return map;
    }

    public static class LocationItem {

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

        public LocationItem(String locationName, int kind, float angle) {

            this.locationName = locationName;
            this.kind = kind;
            this.angle = angle;
            this.sizeX = 1;
            this.sizeY = 1;
        }

        public LocationItem(String locationName, int kind) {

            this.locationName = locationName;
            this.kind = kind;
            this.sizeX = 1;
            this.sizeY = 1;
        }

        public LocationItem(String locationName, int sizeX, int sizeY, int kind, float angle) {

            this.locationName = locationName;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.kind = kind;
            this.angle = angle;
        }
    }
}
