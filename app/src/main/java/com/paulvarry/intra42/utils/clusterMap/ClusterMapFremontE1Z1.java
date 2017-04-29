package com.paulvarry.intra42.utils.clusterMap;

public class ClusterMapFremontE1Z1 {

    public final static int CLUSTER_FREMONT_E1_Z1_WIDTH = 33;
    final static int CLUSTER_FREMONT_E1_Z1_HEIGHT = 22;

    public static LocationItem[][] getFremontCluster1Zone1() {

        LocationItem map[][] = new LocationItem[CLUSTER_FREMONT_E1_Z1_HEIGHT][CLUSTER_FREMONT_E1_Z1_WIDTH];

        String clusterID = "e1z1";
        int r = 0;
        int realR = 1;

        createFremont1z1r1(map, clusterID, r, realR);

        r++;
        ClusterMap.addEmptyRow(map[r], LocationItem.KIND_CORRIDOR);

        r++;
        realR++;
        createFremont1z1r2(map, clusterID, r, realR);

        r += 2;
        ClusterMap.addEmptyRow(map[r], LocationItem.KIND_CORRIDOR);

        r++;
        realR++;
        createFremont1z1r3(map, clusterID, r, realR);

        r += 2;
        ClusterMap.addEmptyRow(map[r], LocationItem.KIND_CORRIDOR);

        r++;
        realR++;
        createFremont1z1r4(map, clusterID, r, realR);

        r += 2;
        ClusterMap.addEmptyRow(map[r], LocationItem.KIND_CORRIDOR);

        r++;
        realR++;
        createFremont1z1r5(map, clusterID, r, realR);

        r += 2;
        ClusterMap.addEmptyRow(map[r], LocationItem.KIND_CORRIDOR);

        r++;
        realR++;
        createFremont1z1r6(map, clusterID, r, realR);

        r += 2;
        ClusterMap.addEmptyRow(map[r], LocationItem.KIND_CORRIDOR);

        r++;
        realR++;
        createFremont1z1r7(map, clusterID, r, realR);

        r += 2;
        ClusterMap.addEmptyRow(map[r], LocationItem.KIND_CORRIDOR);

        r++;
        realR++;
        createFremont1z1r8(map, clusterID, r, realR);

        return map;
    }

    private static void createFremont1z1r1(LocationItem[][] map, String clusterID, int r, int realR) {
        int locationKind;
        String locationName;
        int realP = 7;

        for (int p = 0; p < CLUSTER_FREMONT_E1_Z1_WIDTH; p++) {//r1

            locationKind = LocationItem.KIND_CORRIDOR;
            if (p >= 26 && p < 32) {
                locationKind = LocationItem.KIND_USER;
                realP--;
                locationName = clusterID + "r" + String.valueOf(realR) + "p" + String.valueOf(realP);
            } else
                locationName = null;

            map[r][p] = new LocationItem(locationName, locationKind);
        }
    }

    private static void createFremont1z1r2(LocationItem[][] map, String clusterID, int r, int realR) {
        int locationKind;
        int realP = 0;

        for (int p = CLUSTER_FREMONT_E1_Z1_WIDTH - 1; p >= 0; p--) {//r2

            locationKind = LocationItem.KIND_CORRIDOR;
            if (p >= 5 && p < 33) {
                if (p == 31 || p == 17) {
                    map[r][p] = new LocationItem(null, locationKind);
                    map[r + 1][p] = new LocationItem(null, locationKind);
                } else {
                    ClusterMap.addDoublePost(map, clusterID, LocationItem.KIND_USER, r, p, realP, realR);
                    realP += 2;
                }
            } else
                ClusterMap.addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);
        }
        map[r][12] = new LocationItem(null, LocationItem.KIND_CORRIDOR);
        map[r][13] = new LocationItem(null, LocationItem.KIND_CORRIDOR);
    }

    private static void createFremont1z1r3(LocationItem[][] map, String clusterID, int r, int realR) {
        int locationKind;
        int realP = 0;

        for (int p = CLUSTER_FREMONT_E1_Z1_WIDTH - 1; p >= 0; p--) {//r3

            locationKind = LocationItem.KIND_CORRIDOR;
            if (p >= 1 && p < 33) {
                if (p == 30 || p == 16) {
                    map[r][p] = new LocationItem(null, locationKind);
                    map[r + 1][p] = new LocationItem(null, locationKind);
                } else {
                    ClusterMap.addDoublePost(map, clusterID, LocationItem.KIND_USER, r, p, realP, realR);
                    realP += 2;
                }
            } else
                ClusterMap.addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);

        }
        realP++;
        ClusterMap.addDoublePost(map, clusterID, LocationItem.KIND_USER, LocationItem.KIND_CORRIDOR, r, 0, realP, realR);
    }

    private static void createFremont1z1r4(LocationItem[][] map, String clusterID, int r, int realR) {
        int locationKind;
        String locationName;
        int realP = 1;

        locationName = clusterID + "r" + String.valueOf(realR) + "p" + String.valueOf(realP);
        map[r][CLUSTER_FREMONT_E1_Z1_WIDTH - 1] = new LocationItem(null, LocationItem.KIND_WALL);
        map[r + 1][CLUSTER_FREMONT_E1_Z1_WIDTH - 1] = new LocationItem(locationName, LocationItem.KIND_USER);
        for (int p = CLUSTER_FREMONT_E1_Z1_WIDTH - 2; p >= 0; p--) {//r4

            locationKind = LocationItem.KIND_CORRIDOR;
            if (p >= 4 && p < 33) {
                if (p == 29 || p == 15) {
                    map[r][p] = new LocationItem(null, locationKind);
                    map[r + 1][p] = new LocationItem(null, locationKind);
                } else {
                    if (p > 18)
                        ClusterMap.addDoublePost(map, clusterID, LocationItem.KIND_USER, r, p, realP, realR);
                    else if (p < 18)
                        ClusterMap.addDoublePostFromDown(map, clusterID, LocationItem.KIND_USER, r, p, realP, realR);
                    else {
                        locationName = clusterID + "r" + String.valueOf(realR) + "p" + String.valueOf(realP + 1);
                        map[r][p] = new LocationItem(locationName, LocationItem.KIND_USER);
                        map[r + 1][p] = new LocationItem(null, LocationItem.KIND_WALL);
                        realP--;
                    }
                    realP += 2;
                }
            } else
                ClusterMap.addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);
        }
    }

    private static void createFremont1z1r5(LocationItem[][] map, String clusterID, int r, int realR) {
        int realP = 0;

        for (int p = CLUSTER_FREMONT_E1_Z1_WIDTH - 1; p >= 0; p--) {//r4

            if (p >= 8 && p < 33) {
                if (p == 28 || p == 14)
                    ClusterMap.addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);
                else {
                    ClusterMap.addDoublePostFromDown(map, clusterID, LocationItem.KIND_USER, r, p, realP, realR);
                    realP += 2;
                }
            } else
                ClusterMap.addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);
        }
    }

    private static void createFremont1z1r6(LocationItem[][] map, String clusterID, int r, int realR) {
        int realP = 0;

        for (int p = CLUSTER_FREMONT_E1_Z1_WIDTH - 1; p >= 0; p--) {//r4

            if (p >= 10 && p < 33) {
                if (p == 27 || p == 13)
                    ClusterMap.addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);
                else {
                    ClusterMap.addDoublePostFromDown(map, clusterID, LocationItem.KIND_USER, r, p, realP, realR);
                    realP += 2;
                }
            } else
                ClusterMap.addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);
        }
        map[r + 1][10] = new LocationItem(null, LocationItem.KIND_CORRIDOR);
    }

    private static void createFremont1z1r7(LocationItem[][] map, String clusterID, int r, int realR) {
        String locationName;
        int realP = 1;

        locationName = clusterID + "r" + String.valueOf(realR) + "p" + String.valueOf(realP);
        map[r][CLUSTER_FREMONT_E1_Z1_WIDTH - 1] = new LocationItem(null, LocationItem.KIND_WALL);
        map[r + 1][CLUSTER_FREMONT_E1_Z1_WIDTH - 1] = new LocationItem(locationName, LocationItem.KIND_USER);
        for (int p = CLUSTER_FREMONT_E1_Z1_WIDTH - 2; p >= 0; p--) {//r4

            if (p >= 13 && p < 33) {
                if (p == 26 || p == 12)
                    ClusterMap.addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);
                else {
                    ClusterMap.addDoublePost(map, clusterID, LocationItem.KIND_USER, r, p, realP, realR);
                    realP += 2;
                }
            } else
                ClusterMap.addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);
        }
        map[r + 1][13] = new LocationItem(null, LocationItem.KIND_CORRIDOR);
        map[r + 1][14] = new LocationItem(null, LocationItem.KIND_CORRIDOR);
    }

    private static void createFremont1z1r8(LocationItem[][] map, String clusterID, int r, int realR) {
        int realP = 0;

        for (int p = CLUSTER_FREMONT_E1_Z1_WIDTH - 1; p >= 0; p--) {//r4

            if (p >= 17 && p < 33) {
                if (p == 25 || p == 11)
                    ClusterMap.addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);
                else {
                    ClusterMap.addDoublePostFromDown(map, clusterID, LocationItem.KIND_USER, r, p, realP, realR);
                    realP += 2;
                }
            } else
                ClusterMap.addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);
        }
        for (int i = 0; i < 8; i++) {
            map[r + 1][17 + i] = new LocationItem(null, LocationItem.KIND_CORRIDOR);
        }
    }


}
