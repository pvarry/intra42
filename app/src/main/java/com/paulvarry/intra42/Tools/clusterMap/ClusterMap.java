package com.paulvarry.intra42.Tools.clusterMap;

public class ClusterMap {


    public static LocationItem[][] getFremontCluster(String clusterName) {

        int row = 20;
        int poste = 100;
        String locationName;

        LocationItem map[][] = new LocationItem[row][poste];

        for (int r = 0; r < row; r++) {

            for (int p = 0; p < poste; p++) {

                locationName = clusterName + "r" + String.valueOf(r) + "p" + String.valueOf(p);
                map[r][p] = new LocationItem(locationName, LocationItem.KIND_USER);
            }
        }

        return map;
    }


    public static void addEmptyRow(LocationItem[] map, int KIND_OF_ROW) {
        for (int p = 0; p < ClusterMapFremontE1Z1.CLUSTER_FREMONT_E1_Z1_WIDTH; p++) {
            map[p] = new LocationItem(null, KIND_OF_ROW);
            map[p].sizeY = (float) 0.5;
        }
    }

    public static void addDoublePostFromDown(LocationItem[][] map, String clusterID, int locationKind, int r, int p, int realP, int realR) {
        addDoublePostFromDown(map, clusterID, locationKind, locationKind, r, p, realP, realR);
    }

    private static void addDoublePostFromDown(LocationItem[][] map, String clusterID, int locationKindTop, int locationKindBottom, int r, int p, int realP, int realR) {
        String locationName = null;

        realP++;
        if (locationKindBottom == LocationItem.KIND_USER)
            locationName = clusterID + "r" + String.valueOf(realR) + "p" + String.valueOf(realP);
        map[r + 1][p] = new LocationItem(locationName, locationKindBottom);

        realP++;
        if (locationKindTop == LocationItem.KIND_USER)
            locationName = clusterID + "r" + String.valueOf(realR) + "p" + String.valueOf(realP);
        map[r][p] = new LocationItem(locationName, locationKindTop);
    }

    public static void addDoublePost(LocationItem[][] map, String clusterID, int locationKind, int r, int p, int realP, int realR) {
        addDoublePost(map, clusterID, locationKind, locationKind, r, p, realP, realR);
    }

    public static void addDoublePost(LocationItem[][] map, String clusterID, int locationKindTop, int locationKindBottom, int r, int p, int realP, int realR) {
        String locationName = null;

        realP++;
        if (locationKindTop == LocationItem.KIND_USER)
            locationName = clusterID + "r" + String.valueOf(realR) + "p" + String.valueOf(realP);
        map[r][p] = new LocationItem(locationName, locationKindTop);

        realP++;
        if (locationKindBottom == LocationItem.KIND_USER)
            locationName = clusterID + "r" + String.valueOf(realR) + "p" + String.valueOf(realP);
        map[r + 1][p] = new LocationItem(locationName, locationKindBottom);
    }


}
